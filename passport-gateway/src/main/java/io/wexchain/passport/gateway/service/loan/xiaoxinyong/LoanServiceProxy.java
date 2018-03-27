package io.wexchain.passport.gateway.service.loan.xiaoxinyong;

import io.wexchain.juzix.contract.loan.xiaoxinyong.LoanOrderV1;
import io.wexchain.juzix.contract.loan.xiaoxinyong.LoanServiceV1;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface LoanServiceProxy extends ContractProxy<LoanServiceV1> {

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

	LoanOrderV1 getOrder(String txHash);

	LoanOrderV1 getOrder(Long orderId);
}
