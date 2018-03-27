package io.wexchain.passport.gateway.service.ca;

import io.wexchain.juzix.contract.passport.Ca;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface CaRepo extends ContractProxy<Ca> {
	/**
	 * 
	 * @param signMessageHex
	 * @return tx hash
	 */
	String put(String signMessageHex);

	/**
	 * 
	 * @param signMessageHex
	 * @return tx hash
	 */
	String delete(String signMessageHex);

	byte[] get(String address);

}
