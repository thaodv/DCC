package io.wexchain.passport.gateway.service.cert.dcc;

import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder;
import io.wexchain.juzix.contract.cert.CertService;
import io.wexchain.juzix.contract.cert.CertService3;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface CertServiceProxy extends ContractProxy<CertService3> {

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

	CertOrder getOrder(Long orderId);

	CertData getData(String address);

	CertData getDataAt(String address, Long blockNumber);

}
