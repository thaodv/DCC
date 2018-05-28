package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.godmonth.status.advancer.impl.DoNothingAdvancer;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;

public class CancelAdvancer extends DoNothingAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {
	{
		availableStatus = LoanOrderStatus.CREATED;
		expectedInstruction = LoanOrderInstruction.CANCEL;
		trigger = LoanOrderTrigger.CANCEL;
	}
}