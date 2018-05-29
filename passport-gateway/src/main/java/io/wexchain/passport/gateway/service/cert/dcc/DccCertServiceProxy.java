package io.wexchain.passport.gateway.service.cert.dcc;

import java.math.BigInteger;
import java.util.List;

import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder3;
import io.wexchain.juzix.contract.cert.CertOrderUpdatedEvent;
import io.wexchain.juzix.contract.cert.CertService3;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

public interface DccCertServiceProxy extends ContractProxy<CertService3> {

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
	 * @return tx hash
	 */
	BigInteger getExpectedFee();

	/**
	 *
	 * @param signMessageHex
	 * @return tx hash
	 */
	String revoke(String signMessageHex);

	List<CertOrder3> getOrders(String txHash);

	List<CertOrderUpdatedEvent> getOrderUpdatedEvents(String txHash);

	CertOrder3 getOrder(Long orderId);

	CertData getData(String address);

	CertData getDataAt(String address, Long blockNumber);

}
