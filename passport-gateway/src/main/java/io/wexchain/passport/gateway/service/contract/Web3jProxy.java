package io.wexchain.passport.gateway.service.contract;

public interface Web3jProxy {
	/**
	 *
	 * @param transactionHash
	 * @return hasReceipt
	 */
	ReceiptResult getReceiptResult(String transactionHash, String emitter);

}
