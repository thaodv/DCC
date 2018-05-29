package io.wexchain.passport.gateway.service.cert.dcc;

import io.wexchain.passport.gateway.service.contract.SignMessageValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

public class CertSignMessageValidator extends SignMessageValidator {

	private static final String APPLY_METHOD_ID = "4ffbdca0";
	private static final String PASS_METHOD_ID = "d47c0b2d";
	private static final String REJECT_METHOD_ID = "b8adaa11";
	private static final String REVOKE_METHOD_ID = "74a8f103";

	public void validateApply(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, APPLY_METHOD_ID);
	}

	public void validatePass(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, PASS_METHOD_ID);
	}

	public void validateReject(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, REJECT_METHOD_ID);
	}

	public void validateRevoke(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, REVOKE_METHOD_ID);
	}

}
