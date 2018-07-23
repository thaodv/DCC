package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CancelAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

	{
		availableStatus = LoanOrderStatus.CREATED;
		expectedInstruction = LoanOrderInstruction.CANCEL;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ChainOrderService chainOrderService;

	@Override
	public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(
			LoanOrder loanOrder, LoanOrderInstruction instruction, Object message) {

		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder =
				chainOrderService.getLoanOrder(loanOrder.getChainOrderId());

		logger.info("Cancel loan order -> order id:{}, chain order id:{}, chain order status:{}",
				loanOrder.getId(), chainLoanOrder.getId(), chainLoanOrder.getStatus());

		if (chainLoanOrder.getStatus() == OrderStatus.CANCELLED) {
			return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.CANCEL));
		} else {
			throw new ErrorCodeException("CANCEL_FAIL", "链上订单未取消");
		}
	}
}