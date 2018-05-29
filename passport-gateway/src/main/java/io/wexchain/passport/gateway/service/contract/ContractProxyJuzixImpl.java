package io.wexchain.passport.gateway.service.contract;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import io.wexchain.juzix.contract.commons.EventParserFix;

public class ContractProxyJuzixImpl<T extends Contract> extends Web3jProxyJuzixImpl implements ContractProxy<T> {

	protected T contract;

	protected Resource abiResource;

	@Required
	public void setContract(T contract) {
		this.contract = contract;
	}

	public T getContract() {
		return contract;
	}

	@Override
	public String getContractAddress() {
		return contract.getContractAddress();
	}

	@Override
	public String getAbi() {
		try {
			return IOUtils.toString(abiResource.getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	protected String ethSendRawTransaction(String signMessageHex) {
		try {
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signMessageHex).sendAsync()
					.get(writeTimeoutSecond, TimeUnit.SECONDS);
			if (ethSendTransaction.hasError()) {
				throw new IllegalArgumentException(ethSendTransaction.getError().getMessage());
			}
			return ethSendTransaction.getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException();
		}
	}

	public void setAbiResource(Resource abiResource) {
		this.abiResource = abiResource;
	}

	public <T> List<T> getEvents(String txHash, Event event, Function<EventValues, T> mapper) {
		EthGetTransactionReceipt ethGetTransactionReceipt = null;
		try {
			ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(txHash).sendAsync().get(readTimeoutSecond,
					TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			throw new ContextedRuntimeException(e1);
		}
		TransactionReceipt result = ethGetTransactionReceipt.getResult();
		if (result == null) {
			return null;
		}

		List<EventValues> valueList = EventParserFix.extractEventParameters(event, result, getContractAddress());
		return valueList.stream().map(mapper).collect(Collectors.toList());
	}

	@Override
	public String fastFail(String signMessageHex) {
		// TODO Auto-generated method stub
		return null;
	}
}
