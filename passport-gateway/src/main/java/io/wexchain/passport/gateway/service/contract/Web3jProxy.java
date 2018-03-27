package io.wexchain.passport.gateway.service.contract;

public interface Web3jProxy {
	/**
	 *
	 * @param transactionHash
	 * @return hasReceipt
	 */
	boolean hasReceipt(String transactionHash);

}
