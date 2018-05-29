package io.wexchain.passport.gateway.service.loan.dcc;

import io.wexchain.passport.gateway.service.contract.SignMessageValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

public class LoanSignMessageValidator extends SignMessageValidator {

	private static final String APPLY_METHOD_ID = "9f2530f1";
	private static final String CANCEL_METHOD_ID = "40e58ee5";
	private static final String AUDIT_METHOD_ID = "b173e570";
	private static final String APPROVE_METHOD_ID = "b759f954";
	private static final String REJECT_METHOD_ID = "b8adaa11";
	private static final String DELIVER_METHOD_ID = "f4eb7381";
	private static final String DELIVER_FAILURE_METHOD_ID = "14a0462b";
	private static final String RECEIVE_METHOD_ID = "cba2534f";
	private static final String CONFIRM_REPAY_METHOD_ID = "98df0197";
	private static final String UPDATE_REPAY_DIGEST_METHOD_ID = "552cd31e";

	public void validateApply(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, APPLY_METHOD_ID);
	}

	public void validateCancel(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, CANCEL_METHOD_ID);
	}

	public void validateAudit(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, AUDIT_METHOD_ID);
	}

	public void validateApprove(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, APPROVE_METHOD_ID);
	}

	public void validateReject(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, REJECT_METHOD_ID);
	}

	public void validateDeliver(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, DELIVER_METHOD_ID);
	}

	public void validateDeliverFailure(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, DELIVER_FAILURE_METHOD_ID);
	}

	public void validateReceive(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, RECEIVE_METHOD_ID);
	}

	public void validateConfirmRepay(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, CONFIRM_REPAY_METHOD_ID);
	}

	public void validateUpdateRepayDigest(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, UPDATE_REPAY_DIGEST_METHOD_ID);
	}
}
