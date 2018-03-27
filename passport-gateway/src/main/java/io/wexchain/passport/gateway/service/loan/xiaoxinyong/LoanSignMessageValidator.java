package io.wexchain.passport.gateway.service.loan.xiaoxinyong;

import io.wexchain.passport.gateway.service.contract.SignMessageValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;

public class LoanSignMessageValidator extends SignMessageValidator {

	private static final String APPLY_METHOD_ID = "ed955706";
	private static final String APPROVE_METHOD_ID = "b759f954";
	private static final String REJECT_METHOD_ID = "b8adaa11";
	private static final String DEPOSIT_METHOD_ID = "3c9bd2c8";
	private static final String RECORD_REPAY_METHOD_ID = "de4d5dbe";
	private static final String PAY_OFF_METHOD_ID = "243582ff";
	private static final String FORCE_UPDATE_STATUS_METHOD_ID = "248001df";

	public void validateApply(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, APPLY_METHOD_ID);
	}

	public void validateApprove(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, APPROVE_METHOD_ID);
	}

	public void validateReject(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, REJECT_METHOD_ID);
	}

	public void validateDeposit(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, DEPOSIT_METHOD_ID);
	}

	public void validateRecordRepay(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, RECORD_REPAY_METHOD_ID);
	}

	public void validatePayOff(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, PAY_OFF_METHOD_ID);
	}

	public void validateForceUpdateStatus(Pair<RawTransaction, SignatureData> signMessage, Long currentTime) {
		validateMethod(signMessage, currentTime, FORCE_UPDATE_STATUS_METHOD_ID);
	}
}
