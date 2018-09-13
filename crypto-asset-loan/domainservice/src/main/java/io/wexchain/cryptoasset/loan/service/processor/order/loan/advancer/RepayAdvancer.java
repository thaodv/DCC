package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.alibaba.fastjson.JSON;
import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexyun.open.api.domain.regular.loan.RegularPrepaymentBill;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;
import com.wexyun.open.api.enums.BillStatus;
import com.wexyun.open.api.enums.RepaymentType;
import com.wexyun.open.api.enums.credit2.CreditApplyStatus;
import com.wexyun.open.api.response.QueryResponse4Batch;
import com.wexyun.open.api.response.QueryResponse4Single;
import com.wexyun.open.api.response.TradeOrder4PayResponse;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.api.constant.CollectOrderStatus;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.CollectOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.repository.CollectOrderRepository;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RepayAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

	{
		availableStatus = LoanOrderStatus.DELIVERED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WexyunLoanClient wexyunLoanClient;

    @Autowired
    private CahFunction cahFunction;

    @Autowired
    private CollectOrderRepository collectOrderRepository;

    @Autowired
    private ChainOrderService chainOrderService;

    @Override
    public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(
            LoanOrder loanOrder, LoanOrderInstruction instruction, Object message) {

        logger.info("Loan order repay:{} --> start", loanOrder.getId());

        RepaymentPlan repaymentPlan = wexyunLoanClient.queryFirstRepaymentPlan(loanOrder.getApplyId());
        logger.info("Loan order repay:{} --> query repayment plan", loanOrder.getId(), JSON.toJSONString(repaymentPlan));

        if (repaymentPlan.getStatus() == BillStatus.WAITING_VERIFY) {
            // 还款试算
            BigDecimal repaymentAmount;
            DateTime repayDate = new DateTime(Long.valueOf(loanOrder.getExtParam().get(LoanOrderExtParamKey.REPAY_DATE)));
            if (repayDate.isAfterNow()) {
                RegularPrepaymentBill bill = wexyunLoanClient.queryRegularPrepaymentBill(loanOrder.getApplyId());
                repaymentAmount = bill.getAmount();
            } else {
                repaymentAmount = repaymentPlan.getAmount();
            }

            // 还币地址余额
            BigInteger balance = cahFunction.getBalance(loanOrder.getRepayAddress(), loanOrder.getAssetCode());
            logger.info("Loan order repay:{} --> check repay amount, repay address balance:{}, bill amount:{}",
                    loanOrder.getId(), balance.toString(), repaymentAmount);

            // 检查还款余额
            ErrorCodeValidate.isTrue(balance.compareTo(
                    AmountScaleUtil.cal2Cah(AmountScaleUtil.wexyun2Cal(repaymentAmount))) >= 0,
                    CalErrorCode.BALANCE_INSUFFICIENT);

            // 云金融还款
            doRepay(repaymentPlan, repaymentAmount);

            // 确认还款
            repaymentPlan = confirmRepaymentSuccess(loanOrder);
        }

        if (repaymentPlan.getStatus() == BillStatus.VERIFIED || repaymentPlan.getStatus() == BillStatus.PAY_OFF) {

            BigDecimal repaymentAmount = AmountScaleUtil.wexyun2Cal(repaymentPlan.getAmount());

            // 链上确认还款
            chainOrderService.confirmRepayment(loanOrder.getChainOrderId());
            logger.info("Loan order repay:{} --> chain order repaid:{}",
                    loanOrder.getId(), loanOrder.getChainOrderId());

            // 更新还款摘要
            String repaymentDigest = calcRepaymentSha256(
                    loanOrder, Collections.singletonList(wexyunLoanClient.queryFirstRepaymentPlan(loanOrder.getApplyId())));
            chainOrderService.updateRepaymentDigest(loanOrder.getChainOrderId(), repaymentDigest);
            logger.info("Loan order repay:{} --> update repayment digest:{}",
                    loanOrder.getId(), repaymentDigest);

            if (chainOrderService.getLoanOrder(loanOrder.getChainOrderId()).getStatus() == OrderStatus.REPAID) {
                return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.REPAY, ol -> {
                    ol.setRepayAmount(repaymentAmount);
                    CollectOrder collectOrder = new CollectOrder();
                    collectOrder.setId(loanOrder.getId());
                    collectOrder.setRepayAmount(repaymentAmount);
                    collectOrder.setStatus(CollectOrderStatus.CREATED);
                    collectOrderRepository.save(collectOrder);
                }));
            }
        }
        return null;
    }

    private String calcRepaymentSha256(LoanOrder loanOrder, List<RepaymentPlan> repaymentPlanList) {
        StringBuilder text = new StringBuilder();
        text.append(loanOrder.getExtParam().get(LoanOrderExtParamKey.DELIVER_DATE)); // 放款时间
        for (RepaymentPlan repaymentPlan : repaymentPlanList) {
            text.append(repaymentPlan.getLastRepaymentTime().getTime());
            String repaymentTime = "";
            if (repaymentPlan.getRepaymentTime() != null) {
                repaymentTime = repaymentPlan.getRepaymentTime().getTime() + "";
            }
            text.append(repaymentTime);
            text.append(repaymentPlan.getIssueNumber());
        }
        return DigestUtils.sha256Hex(text.toString());
    }

    private void doRepay(RepaymentPlan repaymentPlan, BigDecimal amount) {
        RepaymentType repaymentType = RepaymentType.NORMAL;
        if (new Date().getTime() <  repaymentPlan.getLastRepaymentTime().getTime()) {
            repaymentType = RepaymentType.ADVANCE;
        }
        wexyunLoanClient.repay(repaymentPlan.getBillId(),
                repaymentPlan.getMemberId(), amount, repaymentType);
    }

    private RepaymentPlan confirmRepaymentSuccess(LoanOrder loanOrder) {
        for (int i = 0; i < 60; i++) {
            RepaymentPlan repaymentPlan = wexyunLoanClient.queryFirstRepaymentPlan(loanOrder.getApplyId());
            if (repaymentPlan.getStatus() == BillStatus.VERIFIED || repaymentPlan.getStatus() == BillStatus.PAY_OFF) {
                return repaymentPlan;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new ContextedRuntimeException(e);
            }
        }
        throw new ContextedRuntimeException("Get repayment result fail, loan order id:" + loanOrder.getId());
    }

}
