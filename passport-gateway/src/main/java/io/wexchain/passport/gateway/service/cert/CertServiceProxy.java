package io.wexchain.passport.gateway.service.cert;

import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder;
import io.wexchain.juzix.contract.cert.CertService;
import io.wexchain.juzix.contract.cert.CertOrderUpdatedEvent;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface CertServiceProxy extends ContractProxy<CertService> {

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
	String pass(String signMessageHex);

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
	String revoke(String signMessageHex);

	CertOrder getOrder(String txHash);

	CertOrderUpdatedEvent getOrderUpdatedEvent(String txHash);

	CertOrder getOrder(Long orderId);

	CertData getData(String address);

	CertData getDataAt(String address, Long blockNumber);

}
