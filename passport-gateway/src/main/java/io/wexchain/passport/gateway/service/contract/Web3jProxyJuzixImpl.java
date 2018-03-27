package io.wexchain.passport.gateway.service.contract;

import java.io.IOException;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;

import juzix.web3j.protocol.CustomWeb3j;

public class Web3jProxyJuzixImpl implements Web3jProxy {
	public static final int DEFAULT_READ_TIMEOUT_SECOND = 5;

	public static final int DEFAULT_WRITE_TIMEOUT_SECOND = 20;

	@Autowired
	protected CustomWeb3j web3j;

	protected int readTimeoutSecond = DEFAULT_READ_TIMEOUT_SECOND;

	protected int writeTimeoutSecond = DEFAULT_WRITE_TIMEOUT_SECOND;

	@Override
	public boolean hasReceipt(String transactionHash) {
		EthGetTransactionReceipt transactionReceipt;
		try {
			transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
		if (transactionReceipt.hasError()) {
			throw new RuntimeException("Error processing request: " + transactionReceipt.getError().getMessage());
		}
		return transactionReceipt.getTransactionReceipt().isPresent();
	}

}
