package io.wexchain.passport.gateway.service.loan.wexCloud;

import io.wexchain.juzix.contract.loan.wexCloud.LoanOrder;
import io.wexchain.juzix.contract.loan.wexCloud.LoanService;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface LoanServiceProxy extends ContractProxy<LoanService> {

	/**
	 * 
	 * @param signMessageHex
	 * @return tx hash
	 */
	String apply(String signMessageHex);

	/**
	 * 
	 * @param signMessageHex
	 * @return tx hash
	 */
	String approve(String signMessageHex);

	/**
	 * 
	 * @param signMessageHex
	 * @return tx hash
	 */
	String reject(String signMessageHex);

	/**
	 * 
	 * @param signMessageHex
	 * @return tx hash
	 */
	String deposit(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String recordRepay(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String payOff(String signMessageHex);

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String forceUpdateStatus(String signMessageHex);

	LoanOrder getOrder(String txHash);

	LoanOrder getOrder(Long orderId);
}
