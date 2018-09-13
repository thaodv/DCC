package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.validator;

import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainCertService;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.constant.ValidateField;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception.DigestException;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.digest.sdk.BankcardCertDigestAlogrithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.util.Arrays;
import java.util.Optional;

public class BankCardCertDigestValidator implements InputDigestValidator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ChainCertService chainCertService;

	@Override
	public void validate(LoanOrder loanOrder, String borrowerAddress) throws DigestException {

		CertData bankCardCertData = chainCertService.getBankCardCertData(borrowerAddress);

		Optional.ofNullable(bankCardCertData).orElseThrow(
				() -> new DigestException(ValidateField.BANK_CARD_CERT_DATA_NOT_FOUND));
		Optional.ofNullable(bankCardCertData.getContent()).orElseThrow(
				() -> new DigestException(ValidateField.BANK_CARD_CERT_CONTENT_NOT_FOUND));
		long now = System.currentTimeMillis();
		if (now > bankCardCertData.getContent().getExpired()) {
			logger.info("Loan order:{}, bank card cert expired, chain expire:{}, now:{}",
					loanOrder.getId(), bankCardCertData.getContent().getExpired(), now);
			throw new DigestException(ValidateField.BANK_CARD_CERT_EXPIRED);
		}

		byte[] digest1 = BankcardCertDigestAlogrithm.digest1(
				loanOrder.getExtParam().get(LoanOrderExtParamKey.BANK_CARD_NO),
				loanOrder.getExtParam().get(LoanOrderExtParamKey.BANK_CARD_MOBILE));

		if (!Arrays.equals(digest1, bankCardCertData.getContent().getDigest1())) {
			logger.info("Loan order:{}, invalid bank card cert digest, chain hash:{}, apply hash:{}",
					loanOrder.getId(),
					Base64Utils.encodeToString(bankCardCertData.getContent().getDigest1()),
					Base64Utils.encodeToString(digest1));
			throw new DigestException(ValidateField.BANK_CARD_CERT_DIGEST);
		}
	}
}
