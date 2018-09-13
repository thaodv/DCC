package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.validator;

import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.constant.ValidateField;
import io.wexchain.dcc.digest.sdk.LoanDigestAlogrithm;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception.DigestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.math.BigDecimal;
import java.util.Arrays;

public class ApplicationDigestValidator implements InputDigestValidator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ChainOrderService chainOrderService;

	@Override
	public void validate(LoanOrder loanOrder, String borrowerAddress) throws DigestException {
		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainOrder =
				chainOrderService.getLoanOrder(loanOrder.getChainOrderId());

		byte[] applicationDigest = LoanDigestAlogrithm.applicationDigest(
				loanOrder.getAssetCode(),
				Integer.valueOf(loanOrder.getExtParam().get(LoanOrderExtParamKey.BORROW_DURATION)),
				loanOrder.getAmount().setScale(4, BigDecimal.ROUND_DOWN),
				loanOrder.getReceiverAddress(),
				Long.valueOf(loanOrder.getExtParam().get(LoanOrderExtParamKey.APPLY_DATE)));
		if (!Arrays.equals(applicationDigest, chainOrder.getApplicationDigest())) {
			logger.info("Loan order:{}, invalid application digest, chain hash:{}, apply hash:{}",
					loanOrder.getId(),
					Base64Utils.encodeToString(chainOrder.getApplicationDigest()),
					Base64Utils.encodeToString(applicationDigest));
			throw new DigestException(ValidateField.APPLICATION_HASH);
		}
	}
}
