package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.cryptoasset.loan.api.constant.AuditingOrderStatus;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.AuditingOrderTrigger;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception.DigestException;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.validator.InputDigestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ValidateAdvancer extends AbstractAdvancer<AuditingOrder, Void, AuditingOrderTrigger> {

	{
		availableStatus = AuditingOrderStatus.CREATED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<InputDigestValidator> inputDigestValidators;

	@Autowired
	private CryptoAssetLoanService cryptoAssetLoanService;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Override
	public AdvancedResult<AuditingOrder, AuditingOrderTrigger> advance(AuditingOrder auditingOrder, Void instruction,
			Object message) {

		LoanOrder loanOrder = cryptoAssetLoanService.getLoanOrder(auditingOrder.getId());
		String borrowerAddress = wexyunLoanClient.getAddressById(loanOrder.getMemberId());
		logger.info("Validate auditing order:{} --> loan order id:{}, borrower address:{}",
				auditingOrder.getId(), loanOrder.getId(), borrowerAddress);
		try {
			for (InputDigestValidator inputDigestValidator : inputDigestValidators) {
				inputDigestValidator.validate(loanOrder, borrowerAddress);
				logger.info("Validate auditing order:{} --> {} passed",
						auditingOrder.getId(), inputDigestValidator.getClass().getName());
			}
			return new AdvancedResult<>(new TriggerBehavior<>(
					 AuditingOrderTrigger.VALID));
		} catch (DigestException e) {
			return new AdvancedResult<>(new TriggerBehavior<>(
					AuditingOrderTrigger.REJECT, model1 -> model1.setFailCode(e.getField())));
		}
	}

	public void setInputDigestValidators(List<InputDigestValidator> inputDigestValidators) {
		this.inputDigestValidators = inputDigestValidators;
	}
}
