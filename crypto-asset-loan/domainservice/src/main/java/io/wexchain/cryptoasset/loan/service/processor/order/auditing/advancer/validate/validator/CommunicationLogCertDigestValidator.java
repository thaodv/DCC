package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.validator;

import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainCertService;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.constant.ValidateField;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception.DigestException;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.digest.sdk.ComunncationLogCertDigestAlogrithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.util.Arrays;
import java.util.Optional;

public class CommunicationLogCertDigestValidator implements InputDigestValidator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ChainCertService chainCertService;

	@Override
	public void validate(LoanOrder loanOrder, String borrowerAddress) throws DigestException {
		CertData communicationLogCertData = chainCertService.getCommunicationLogCertData(borrowerAddress);

		Optional.ofNullable(communicationLogCertData).orElseThrow(
				() -> new DigestException(ValidateField.COMMUNICATION_LOG_CERT_DATA_NOT_FOUND));
		Optional.ofNullable(communicationLogCertData.getContent()).orElseThrow(
				() -> new DigestException(ValidateField.COMMUNICATION_LOG_CERT_CONTENT_NOT_FOUND));
		long now = System.currentTimeMillis();
		if (now > communicationLogCertData.getContent().getExpired()) {
			logger.info("Loan order:{}, communication log cert expired, chain expire:{}, now:{}",
					loanOrder.getId(), communicationLogCertData.getContent().getExpired(), now);
			throw new DigestException(ValidateField.COMMUNICATION_LOG_CERT_EXPIRED);
		}

		byte[] digest1 = ComunncationLogCertDigestAlogrithm.digest1(
				loanOrder.getExtParam().get(LoanOrderExtParamKey.COMMUNICATION_LOG));

		if (!Arrays.equals(digest1, communicationLogCertData.getContent().getDigest1())) {
			logger.info("Loan order:{}, invalid communication log cert hash, chain hash:{}, apply hash:{}",
					loanOrder.getId(),
					Base64Utils.encodeToString(communicationLogCertData.getContent().getDigest1()),
					Base64Utils.encodeToString(digest1));
			throw new DigestException(ValidateField.COMMUNICATION_LOG_CERT_DIGEST);
		}
	}
}
