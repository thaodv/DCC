package io.wexchain.passport.gateway.service.contract;

public interface ContractProxy<T> extends Web3jProxy {
	/**
	 * 
	 * @return
	 */
	String getContractAddress();

	/**
	 * 
	 * @return
	 */
	String getAbi();


}
