package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.validator;

import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception.DigestException;

public interface InputDigestValidator {

	void validate(LoanOrder loanOrder, String borrowerAddress) throws DigestException;

}
