package io.wexchain.passport.gateway.service.contract;

import java.math.BigInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

import com.google.common.primitives.Longs;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;

import io.wexchain.juzix.contract.cert.CertContent;
import io.wexchain.juzix.contract.commons.ContractParam;
import io.wexchain.passport.gateway.ctrlr.ca.CaErrorCode;

public class SignMessageValidator {

	private static final Logger logger = LoggerFactory.getLogger(SignMessageValidator.class);

	public static final int NONCE_LENGTH = 32;

	public static final int DEFAULT_NONCE_EXPIRED_HOURS = 1;

	protected ContractParam<CertContent> contractParam;

	protected int nonceExpiredHours = DEFAULT_NONCE_EXPIRED_HOURS;

	public void validateMethod(Pair<RawTransaction, SignatureData> signMessage, Long currentTime, String methodId) {
		validateSign(signMessage.getRight());
		validateExcludeData(signMessage.getLeft(), currentTime);
		validateData(signMessage.getLeft().getData(), methodId);
	}

	protected void validateExcludeData(RawTransaction rawTransaction, Long currentTime) {
		validateNonce(rawTransaction.getNonce(), currentTime);
		validateGasPrice(rawTransaction.getGasPrice());
		validateGasLimit(rawTransaction.getGasLimit());
		validateTo(rawTransaction.getTo());
		validateValue(rawTransaction.getValue());
	}

	public void validateSign(SignatureData signatureData) {
		ErrorCodeValidate.isTrue(ArrayUtils.isNotEmpty(signatureData.getS()), CaErrorCode.SIGN_MESSAGE_INVALID,
				"sign s invalid");
		ErrorCodeValidate.isTrue(ArrayUtils.isNotEmpty(signatureData.getR()), CaErrorCode.SIGN_MESSAGE_INVALID,
				"sign r invalid");

	}

	protected static void validateData(String data, String method) {
		String actualMethodId = StringUtils.substring(data, 0, 8);
		ErrorCodeValidate.isTrue(actualMethodId.equalsIgnoreCase(method), CaErrorCode.SIGN_MESSAGE_INVALID,
				"methodId invalid");
	}

	protected static void validateValue(BigInteger value) {
		ErrorCodeValidate.isTrue(value.compareTo(BigInteger.ZERO) == 0, CaErrorCode.SIGN_MESSAGE_INVALID,
				"value invalid");
	}

	protected void validateTo(String to) {
		ErrorCodeValidate.isTrue(to.equalsIgnoreCase(contractParam.getContractAddress()),
				CaErrorCode.SIGN_MESSAGE_INVALID, "to invalid");
	}

	protected void validateNonce(BigInteger nonce, Long currentTime) {
		byte[] byteArray = nonce.toByteArray();
		ErrorCodeValidate.isTrue(byteArray.length <= NONCE_LENGTH, CaErrorCode.SIGN_MESSAGE_INVALID,
				String.format("nonce length exceeded, input:%s, max:%s", byteArray.length, NONCE_LENGTH));
		if (byteArray.length < NONCE_LENGTH) {
			byteArray = ArrayUtils.addAll(new byte[NONCE_LENGTH - byteArray.length], byteArray);
		}
		long timestamp = Longs.fromByteArray(byteArray);
		if (logger.isTraceEnabled()) {
			DateTime currentDt = new DateTime(currentTime);
			DateTime inputDt = new DateTime(timestamp);
			logger.trace("inputDt:{}, currentDt:{}", inputDt, currentDt);
		}
		long duration = Math.abs(timestamp - currentTime);
		Duration millis = Duration.millis(duration);
		ErrorCodeValidate.isTrue(!millis.isLongerThan(Period.hours(nonceExpiredHours).toStandardDuration()),
				CaErrorCode.SIGN_MESSAGE_INVALID, "nonce expired");

	}

	protected void validateGasPrice(BigInteger gasPrice) {
		ErrorCodeValidate.isTrue(gasPrice.compareTo(contractParam.getMaxGasPrice()) <= 0,
				CaErrorCode.SIGN_MESSAGE_INVALID,
				String.format("gasPrice exceeded, input:%s, max:%s", gasPrice, contractParam.getMaxGasPrice()));
	}

	protected void validateGasLimit(BigInteger gasLimit) {
		ErrorCodeValidate.isTrue(gasLimit.compareTo(contractParam.getMaxGasLimit()) <= 0,
				CaErrorCode.SIGN_MESSAGE_INVALID,
				String.format("gasLimit exceeded, input:%s, max:%s", gasLimit, contractParam.getMaxGasLimit()));
	}

	public void setNonceExpiredHours(int nonceExpiredHours) {
		this.nonceExpiredHours = nonceExpiredHours;
	}

	@Required
	public void setContractParam(ContractParam<CertContent> contractParam) {
		this.contractParam = contractParam;
	}

}
