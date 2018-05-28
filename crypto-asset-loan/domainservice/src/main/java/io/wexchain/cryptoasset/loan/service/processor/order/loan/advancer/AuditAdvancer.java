package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.enums.DurationType;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.UnretryableCommand;
import io.wexchain.cryptoasset.loan.service.constant.CommandName;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.command.CommandIndex;
import io.wexchain.cryptoasset.loan.service.function.command.UnretryableCommandFunction;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2ApplyAddRequest;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuditAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

    private Logger logger = LoggerFactory.getLogger(AuditAdvancer.class);

	{
		availableStatus = LoanOrderStatus.CREATED;
	}

	@Autowired
	private UnretryableCommandFunction unretryableCommandFunction;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Autowired
	private ChainOrderService chainOrderService;

    @Override
    public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(
            LoanOrder loanOrder, LoanOrderInstruction instruction, Object message) {

        // 审核链上订单
        chainOrderService.audit(loanOrder.getChainOrderId());

        UnretryableCommand command = unretryableCommandFunction.prepareCommand(
                new CommandIndex(LoanOrder.TYPE_REF, loanOrder.getId(), CommandName.CMD_APPLY), null);

        if (command.getStatus().equals(GeneralCommandStatus.CREATED.name())) {
            // 查询进件申请
            QueryResponse4Single<Credit2Apply> queryResult = wexyunLoanClient.getApplyOrder(String.valueOf(command.getId()));
            if (queryResult.isSuccess()) {
                Credit2Apply applyOrder = queryResult.getContent();
                if (applyOrder == null) {
                    return submitApply(loanOrder, command);
                } else {
                    return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.AUDIT, innerLoanOrder -> {
                        innerLoanOrder.setApplyId(applyOrder.getApplyId());
                        unretryableCommandFunction.updateStatus(command, GeneralCommandStatus.SUCCESS.name());
                    }), NextOperation.PAUSE);
                }
            } else {
                return auditFail(loanOrder, queryResult, command);
            }
        }
        // 交易成功
        if (command.getStatus().equals(GeneralCommandStatus.SUCCESS.name())) {
            return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.AUDIT), NextOperation.PAUSE);
        }
        // 交易失败
        if (command.getStatus().equals(GeneralCommandStatus.FAILURE.name())) {
            return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.FAIL));
        }

        return null;
    }

    private Credit2ApplyAddRequest buildRequest(LoanOrder loanOrder, Long commandId) {
        Map<String, String> extParam = loanOrder.getExtParam();
        Credit2ApplyAddRequest applyAddRequest = new Credit2ApplyAddRequest();
        applyAddRequest.setBorrowerId(loanOrder.getMemberId());
        applyAddRequest.setBorrowAmount(AmountScaleUtil.cal2Wexyun(loanOrder.getAmount()));
        applyAddRequest.setBorrowDuration(
                Integer.valueOf(extParam.get(LoanOrderExtParamKey.BORROW_DURATION)));
        applyAddRequest.setDurationType(
                DurationType.valueOf(extParam.get(LoanOrderExtParamKey.BORROW_DURATION_UNIT)));
        applyAddRequest.setExternalApplyId(String.valueOf(commandId));
        applyAddRequest.setReceiveMemberId(loanOrder.getMemberId());
        applyAddRequest.setLoanType(extParam.get(LoanOrderExtParamKey.LOAN_TYPE));

        applyAddRequest.setSourceIdentity(extParam.get(LoanOrderExtParamKey.APP_IDENTITY));
        applyAddRequest.setApplyDate(
                new Date(Long.valueOf(extParam.get(LoanOrderExtParamKey.APPLY_DATE))));
        Map<String, String> itemDetailMap = new HashMap<>();
        itemDetailMap.put("loanProductId", extParam.get(LoanOrderExtParamKey.LOAN_PRODUCT_ID));
        itemDetailMap.put("agreementExptectAnnuRate",
                extParam.get(LoanOrderExtParamKey.EXPECT_ANNUAL_RATE));
        itemDetailMap.put("agreementRepayMode", extParam.get(LoanOrderExtParamKey.REPAY_MODE));

        applyAddRequest.setBorrowName(extParam.get(LoanOrderExtParamKey.BORROW_NAME));
        applyAddRequest.setCertNo(extParam.get(LoanOrderExtParamKey.ID_NO));
        applyAddRequest.setBankCard(extParam.get(LoanOrderExtParamKey.BANK_CARD_NO));
        applyAddRequest.setMobile(extParam.get(LoanOrderExtParamKey.MOBILE));
        applyAddRequest.setBankMobile(extParam.get(LoanOrderExtParamKey.BANK_MOBILE));

        applyAddRequest.setItemDetailMap(itemDetailMap);
        return applyAddRequest;
    }

    private AdvancedResult<LoanOrder, LoanOrderTrigger> submitApply(LoanOrder loanOrder, UnretryableCommand command) {
        QueryResponse4Single<Credit2ApplyAddResult> applyResult =
                wexyunLoanClient.apply(buildRequest(loanOrder, command.getId()));
        if (applyResult.isSuccess()) {
            return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.AUDIT, innerLoanOrder -> {
                innerLoanOrder.setApplyId(String.valueOf(applyResult.getContent().getApplyId()));
                unretryableCommandFunction.updateStatus(command, GeneralCommandStatus.SUCCESS.name());
            }), NextOperation.PAUSE);
        } else {
            return auditFail(loanOrder, applyResult, command);
        }
    }

    private AdvancedResult<LoanOrder, LoanOrderTrigger> auditFail(
            LoanOrder loanOrder, BaseResponse response, UnretryableCommand command) {

        chainOrderService.reject(loanOrder.getChainOrderId());

        return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.FAIL, innerLoanOrder -> {
            innerLoanOrder.setFailCode(response.getResponseCode());
            innerLoanOrder.setFailMessage(response.getResponseMessage());
            unretryableCommandFunction.updateStatus(command, GeneralCommandStatus.FAILURE.name());
        }), NextOperation.PAUSE);
    }


}
