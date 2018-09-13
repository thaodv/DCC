package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.validator;

import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.ext.integration.ufs.UfsClient;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainCertService;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.constant.ValidateField;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.validate.exception.DigestException;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.digest.sdk.IdCertDigestAlogrithm;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class IdCertDigestValidator implements InputDigestValidator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UfsClient ufsClient;

	@Autowired
	private ChainOrderService chainOrderService;

	@Autowired
	private ChainCertService chainCertService;

	@Override
	public void validate(LoanOrder loanOrder, String borrowerAddress) throws DigestException {
		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainOrder =
				chainOrderService.getLoanOrder(loanOrder.getChainOrderId());

		Map<String, String> extParam = loanOrder.getExtParam();

		// 校验进件订单 id hash
		byte[] digest1 = IdCertDigestAlogrithm.digest1(
				extParam.get(LoanOrderExtParamKey.BORROWER_NAME),
				extParam.get(LoanOrderExtParamKey.ID_CARD_NO));
		logger.info("Loan order:{}, validate id hash, chain hash:{}, apply hash:{}",
				loanOrder.getId(),
				Base64Utils.encodeToString(chainOrder.getIdHash()),
				Base64Utils.encodeToString(digest1));
		if (!Arrays.equals(digest1, chainOrder.getIdHash())) {
			throw new DigestException(ValidateField.ID_HASH);
		}

		// 校验实名认证 digest1
		CertData idCertData = chainCertService.getIdCertData(borrowerAddress);
		Optional.ofNullable(idCertData).orElseThrow(
				() -> new DigestException(ValidateField.ID_CERT_DATA_NOT_FOUND));
		Optional.ofNullable(idCertData.getContent()).orElseThrow(
				() -> new DigestException(ValidateField.ID_CERT_CONTENT_NOT_FOUND));
		long now = System.currentTimeMillis();
		if (now > idCertData.getContent().getExpired()) {
			logger.info("Loan order:{}, id cert expired, chain expire:{}, now:{}",
					loanOrder.getId(), idCertData.getContent().getExpired(), now);
			throw new DigestException(ValidateField.ID_CERT_EXPIRED);
		}

		logger.info("Loan order:{}, validate id cert digest 1, chain hash:{}, apply hash:{}",
				loanOrder.getId(),
				Base64Utils.encodeToString(idCertData.getContent().getDigest1()),
				Base64Utils.encodeToString(digest1));

		if (!Arrays.equals(digest1, idCertData.getContent().getDigest1())) {
			logger.info("Loan order:{}, invalid id cert digest 1, chain hash:{}, apply hash:{}",
					loanOrder.getId(),
					Base64Utils.encodeToString(idCertData.getContent().getDigest1()),
					Base64Utils.encodeToString(digest1));
			throw new DigestException(ValidateField.ID_CERT_DIGEST_1);
		}

		// 校验实名认证 digest2
		byte[] idCardFront = getUfsFileByteArray(extParam.get(LoanOrderExtParamKey.ID_CARD_FRONT_PIC_UFS_PATH));
		byte[] idCardBack = getUfsFileByteArray(extParam.get(LoanOrderExtParamKey.ID_CARD_BACK_PIC_UFS_PATH));
		byte[] face = getUfsFileByteArray(extParam.get(LoanOrderExtParamKey.FACE_PIC_UFS_PATH));
		byte[] digest2 = IdCertDigestAlogrithm.digest2(digest1, idCardFront, idCardBack, face);

		logger.info("Loan order:{}, validate id cert digest 2, chain hash:{}, apply hash:{}",
				loanOrder.getId(),
				Base64Utils.encodeToString(idCertData.getContent().getDigest2()),
				Base64Utils.encodeToString(digest2));

		if (!Arrays.equals(digest2, idCertData.getContent().getDigest2())) {
			logger.info("Loan order:{}, invalid id cert digest 2, chain hash:{}, apply hash:{}",
					loanOrder.getId(),
					Base64Utils.encodeToString(idCertData.getContent().getDigest2()),
					Base64Utils.encodeToString(digest2));
			throw new DigestException(ValidateField.ID_CERT_DIGEST_2);
		}
	}

	private byte[] getUfsFileByteArray(String ufsFilePath) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			ufsClient.downloadFile(bos, ufsFilePath);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
	}
}
