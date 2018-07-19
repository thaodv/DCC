package io.wexchain.cryptoasset.loan.service.function.entry;

import com.godmonth.status.transitor.bean.intf.StatusEntry;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.ext.integration.message.impl.LoanOrderUpdateEventRouter;
import io.wexchain.notify.domain.dcc.MessageBusinessType;
import io.wexchain.notify.domain.dcc.OrderUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AuditOrderNotifyEntry
 *
 * @author zhengpeng
 */
public class AuditOrderNotifyEntry<T> implements StatusEntry<T> {

	private Logger logger = LoggerFactory.getLogger(AuditOrderNotifyEntry.class);

	@Autowired
	private LoanOrderUpdateEventRouter loanOrderUpdateEventRouter;

	@Override
	public void nextStatusEntry(T model) {
		logger.info("enter next status entry, model class is {}", model.getClass().getName());
		if (model instanceof LoanOrder) {
			LoanOrder loanOrder = (LoanOrder) model;
			if (loanOrder.getStatus() != LoanOrderStatus.CREATED) {

				loanOrderUpdateEventRouter.route(new OrderUpdatedEvent(loanOrder.getReceiverAddress(),loanOrder.getId(),loanOrder.getStatus().name()));
					logger.info("publish loanOrder data message, order id is {}, receiverAddress is {}, status is {}",
							loanOrder.getId(), loanOrder.getReceiverAddress(), loanOrder.getStatus());
				}
			}
		}
}
