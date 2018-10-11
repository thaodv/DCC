package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexyun.open.api.enums.credit2.CreditApplyStatus;
import com.wexyun.open.api.enums.credit2.CreditProcessStatus;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.loan.api.constant.AuditingOrderStatus;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.AuditingOrderTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RiskingAdvancer extends AbstractAdvancer<AuditingOrder, Void, AuditingOrderTrigger> {
	{
		availableStatus = AuditingOrderStatus.APPLIED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Override
	public AdvancedResult<AuditingOrder, AuditingOrderTrigger> advance(AuditingOrder auditingOrder, Void instruction,
			Object message) {

		QueryResponse4Single<Credit2Apply> response4Single =
                wexyunLoanClient.getApplyOrder(auditingOrder.getApplyId());

        if (response4Single.isSuccess()) {
            Credit2Apply applyOrder = response4Single.getContent();
            if (checkApplyStatusSuccess(applyOrder)) {
                return new AdvancedResult<>(new TriggerBehavior<>(AuditingOrderTrigger.APPROVE));
            }
            if (applyOrder.getProcessStatus() == CreditProcessStatus.FAIL) {

                return new AdvancedResult<>(new TriggerBehavior<>(AuditingOrderTrigger.REJECT));
            }
        } else {
            logger.info("Query apply order fail, fail code:{}, fail message:{}",
                    response4Single.getResponseCode(), response4Single.getResponseMessage());
        }
        return null;
	}

	private boolean checkApplyStatusSuccess(Credit2Apply applyOrder) {
		return applyOrder.getStatus() == CreditApplyStatus.SIGN
				|| applyOrder.getStatus() == CreditApplyStatus.EFFECTIVED
				|| applyOrder.getStatus() == CreditApplyStatus.ARCHIVED;
	}

}
