package io.wexchain.passport.gateway.service.cert.dcc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.godmonth.eth.rlp.web3j.SignMessageParser;

import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder3;
import io.wexchain.juzix.contract.cert.CertOrderUpdatedEvent;
import io.wexchain.juzix.contract.cert.CertService3;
import io.wexchain.juzix.contract.cert.CertService3.OrderUpdatedEventResponse;
import io.wexchain.juzix.contract.cert.CertServiceDataParser;
import io.wexchain.juzix.contract.commons.EventParserFix;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;

public class CertServiceProxyJuzixImpl extends ContractProxyJuzixImpl<CertService3> implements DccCertServiceProxy {

	private DccCertSignMessageValidator signMessageValidator;

	@Override
	public String getContractAddress() {
		return contract.getContractAddress();
	}

	@Override
	public String apply(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateApply(parseFull, System.currentTimeMillis());

		try {
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signMessageHex).sendAsync()
					.get(writeTimeoutSecond, TimeUnit.SECONDS);
			if (ethSendTransaction.hasError()) {
				throw new IllegalArgumentException(ethSendTransaction.getError().getMessage());
			}
			return ethSendTransaction.getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}

	}

	@Override
	public String pass(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validatePass(parseFull, System.currentTimeMillis());
		try {
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signMessageHex).sendAsync()
					.get(writeTimeoutSecond, TimeUnit.SECONDS);
			if (ethSendTransaction.hasError()) {
				throw new IllegalArgumentException(ethSendTransaction.getError().getMessage());
			}
			return ethSendTransaction.getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	@Override
	public String reject(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateReject(parseFull, System.currentTimeMillis());
		try {
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signMessageHex).sendAsync()
					.get(writeTimeoutSecond, TimeUnit.SECONDS);
			if (ethSendTransaction.hasError()) {
				throw new IllegalArgumentException(ethSendTransaction.getError().getMessage());
			}
			return ethSendTransaction.getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	@Override
	public String revoke(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateRevoke(parseFull, System.currentTimeMillis());

		try {
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signMessageHex).sendAsync()
					.get(writeTimeoutSecond, TimeUnit.SECONDS);
			if (ethSendTransaction.hasError()) {
				throw new IllegalArgumentException(ethSendTransaction.getError().getMessage());
			}
			return ethSendTransaction.getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	@Override
	public CertOrder3 getOrder(Long orderId) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getOrder(new Uint256(BigInteger.valueOf(orderId))).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return CertServiceDataParser.parseOrder3(list, orderId);
	}

	@Override
	public List<CertOrder3> getOrders(String txHash) {
		List<CertOrderUpdatedEvent> orderUpdatedEvents = getOrderUpdatedEvents(txHash);
		return orderUpdatedEvents.stream().map(CertOrderUpdatedEvent::getOrderId).map(this::getOrder)
				.collect(Collectors.toList());
	}

	@Override
	public CertData getDataAt(String address, Long blockNumber) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getDataAt(new Address(address), new Uint256(BigInteger.valueOf(blockNumber)))
					.get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return CertServiceDataParser.parseData(list);
	}

	@Override
	public BigInteger getExpectedFee() {
		Uint256 expectedFee = null;
		try {
			expectedFee = contract.getExpectedFeeDcc().get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		if (expectedFee != null) {
			return expectedFee.getValue();
		}
		return null;
	}

	@Override
	public CertData getData(String address) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getData(new Address(address)).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return CertServiceDataParser.parseData(list);
	}

	@Required
	public void setSignMessageValidator(DccCertSignMessageValidator signMessageValidator) {
		this.signMessageValidator = signMessageValidator;
	}

	@Override
	public List<CertOrderUpdatedEvent> getOrderUpdatedEvents(String txHash) {
		EthGetTransactionReceipt ethGetTransactionReceipt = null;
		try {
			ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(txHash).sendAsync().get(readTimeoutSecond,
					TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			throw new ContextedRuntimeException(e1);
		}
		TransactionReceipt result = ethGetTransactionReceipt.getResult();
		if (result == null) {
			return new ArrayList<CertOrderUpdatedEvent>();
		}

		final Event event = new Event("orderUpdated", Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
		}, new TypeReference<Uint256>() {
		}), Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {
		}));

		List<EventValues> valueList = EventParserFix.extractEventParameters(event, result, getContractAddress());
		ArrayList<OrderUpdatedEventResponse> responses = new ArrayList<OrderUpdatedEventResponse>(valueList.size());
		for (EventValues eventValues : valueList) {
			OrderUpdatedEventResponse typedResponse = new OrderUpdatedEventResponse();
			typedResponse.applicant = (Address) eventValues.getIndexedValues().get(0);
			typedResponse.orderId = (Uint256) eventValues.getIndexedValues().get(1);
			typedResponse.status = (Uint8) eventValues.getNonIndexedValues().get(0);
			responses.add(typedResponse);
		}
		return responses.stream().map(CertServiceDataParser::convert).collect(Collectors.toList());
	}

}
