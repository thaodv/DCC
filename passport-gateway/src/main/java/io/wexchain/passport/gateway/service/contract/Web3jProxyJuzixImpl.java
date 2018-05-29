package io.wexchain.passport.gateway.service.contract;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import juzix.web3j.protocol.CustomWeb3j;

public class Web3jProxyJuzixImpl implements Web3jProxy {
	public static final int DEFAULT_READ_TIMEOUT_SECOND = 5;

	public static final int DEFAULT_WRITE_TIMEOUT_SECOND = 20;

	@Autowired
	protected CustomWeb3j web3j;

	protected int readTimeoutSecond = DEFAULT_READ_TIMEOUT_SECOND;

	protected int writeTimeoutSecond = DEFAULT_WRITE_TIMEOUT_SECOND;

	@Override
	public ReceiptResult getReceiptResult(String transactionHash, String txFrom) {
		try {
			Optional<TransactionReceipt> transactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send()
					.getTransactionReceipt();
			boolean exits = transactionReceipt.isPresent();
			if (!exits) {
				return new ReceiptResult(false, null);
			}
			Transaction transaction = web3j.ethGetTransactionByHash(transactionHash).send().getTransaction().get();
			if (StringUtils.isNotBlank(txFrom)) {
				String actualTxFrom = transaction.getFrom();
				if (!txFrom.equalsIgnoreCase(actualTxFrom)) {
					throw new IllegalStateException("incorrect txFrom");
				}
			}
			BigInteger gasUsed = transactionReceipt.get().getGasUsed();
			BigInteger gas = transaction.getGas();
			return new ReceiptResult(true, gasUsed.compareTo(gas) < 0);
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}

	}

}
