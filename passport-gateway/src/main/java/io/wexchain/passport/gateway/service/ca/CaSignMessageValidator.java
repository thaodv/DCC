package io.wexchain.passport.gateway.service.ca;

import org.apache.commons.lang3.tuple.Pair;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

import io.wexchain.passport.gateway.service.contract.SignMessageValidator;

public class CaSignMessageValidator extends SignMessageValidator {
	public static final String PUT_METHOD_ID = "cee1ac8f";

	public static final String DELETE_METHOD_ID = "e8510fc9";

	public void validatePut(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, PUT_METHOD_ID);
	}

	public void validateDelete(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, DELETE_METHOD_ID);
	}

}
