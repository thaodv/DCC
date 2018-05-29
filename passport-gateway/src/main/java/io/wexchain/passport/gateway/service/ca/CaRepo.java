package io.wexchain.passport.gateway.service.ca;

import java.util.List;

import io.wexchain.juzix.contract.passport.Ca;
import io.wexchain.juzix.contract.passport.KeyDeletedEvent;
import io.wexchain.juzix.contract.passport.KeyPutEvent;
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

	List<KeyPutEvent> getKeyPutEvents(String txHash);

	List<KeyDeletedEvent> getKeyDeletedEvents(String txHash);

}
