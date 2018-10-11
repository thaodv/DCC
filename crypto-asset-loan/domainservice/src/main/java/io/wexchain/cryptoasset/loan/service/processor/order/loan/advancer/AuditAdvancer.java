package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

	{
		availableStatus = LoanOrderStatus.CREATED;
	}

    private Logger logger = LoggerFactory.getLogger(AuditAdvancer.class);

	@Autowired
	private ChainOrderService chainOrderService;

    @Override
    public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(
            LoanOrder loanOrder, LoanOrderInstruction instruction, Object message) {

        logger.info("Loan order: {} --> AUDIT, start", loanOrder.getId());

        if (chainOrderService.getLoanOrder(loanOrder.getChainOrderId()).getStatus() == OrderStatus.CANCELLED) {
            logger.info("Loan order: {} --> AUDIT, cancel order", loanOrder.getId());
            return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.CANCEL));
        }

        logger.info("Loan order: {} --> AUDIT, audit chain order", loanOrder.getId());
        chainOrderService.audit(loanOrder.getChainOrderId());

        if (chainOrderService.getLoanOrder(loanOrder.getChainOrderId()).getStatus() == OrderStatus.AUDITING) {
            logger.info("Loan order: {} --> AUDIT, audit order", loanOrder.getId());
            return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.AUDIT));
        }

        return null;
    }
}
