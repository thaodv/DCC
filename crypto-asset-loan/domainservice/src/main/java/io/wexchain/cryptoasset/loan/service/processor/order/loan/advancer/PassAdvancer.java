package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.alibaba.fastjson.JSON;
import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexyun.open.api.enums.credit2.CreditApplyStatus;
import com.wexyun.open.api.enums.credit2.CreditProcessStatus;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.RebateService;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PassAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

    private Logger logger = LoggerFactory.getLogger(getClass());

	{
		availableStatus = LoanOrderStatus.AUDITING;
	}

    @Autowired
    private WexyunLoanClient wexyunLoanClient;

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private RebateService rebateService;

    @Override
    public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(
            LoanOrder loanOrder, LoanOrderInstruction instruction, Object message) {

        QueryResponse4Single<Credit2Apply> response4Single =
                wexyunLoanClient.getApplyOrder(loanOrder.getApplyId());

        logger.info(JSON.toJSONString(response4Single));

        if (response4Single.isSuccess()) {
            Credit2Apply applyOrder = response4Single.getContent();
            if (applyOrder.getStatus() == CreditApplyStatus.SIGN) {
                chainOrderService.approve(loanOrder.getChainOrderId());
                return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.APPROVE, lo -> {
                    //BigDecimal interest = AmountScaleUtil.wexyun2Cal(applyOrder.getDebtAgreement().getExpectInterest());
                    //lo.getExtParam().put(LoanOrderExtParamKey.EXPECT_LOAN_INTEREST, interest.toString());
                    lo.getExtParam().put(LoanOrderExtParamKey.EXPECT_LOAN_INTEREST, "0.0000");
                }));
            }
            if (applyOrder.getProcessStatus() == CreditProcessStatus.FAIL) {
                chainOrderService.reject(loanOrder.getChainOrderId());
                
                rebateService.rejectOrDeliverFail(loanOrder.getId());
                return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.REJECT));
            }
        } else {
            logger.error("Query apply order fail, fail code:{}, fail message:{}",
                    response4Single.getResponseCode(), response4Single.getResponseMessage());
        }
        return null;
    }
}
