package io.wexchain.passport.gateway.service.cert;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.godmonth.eth.rlp.web3j.SignMessageParser;

import io.wexchain.juzix.contract.cert.CertData;
import io.wexchain.juzix.contract.cert.CertOrder;
import io.wexchain.juzix.contract.cert.CertService;
import io.wexchain.juzix.contract.cert.CertService.OrderUpdatedEventResponse;
import io.wexchain.juzix.contract.cert.DataParser;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;

public class CertServiceProxyJuzixImpl extends ContractProxyJuzixImpl<CertService>
		implements CertServiceProxy, InitializingBean {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	private CertSignMessageValidator signMessageValidator;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isNotBlank(name)) {
			String tempName = contract.name().get().getValue();
			Validate.isTrue(tempName.equals(name), "期望值:" + name + "实际值" + tempName);
		}
	}

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
	public CertOrder getOrder(Long orderId) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getOrder(new Uint256(BigInteger.valueOf(orderId))).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return DataParser.parseOrder(list, orderId);
	}

	@Override
	public CertOrder getOrder(String txHash) {
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

		List<OrderUpdatedEventResponse> orderUpdatedEvents = contract.getOrderUpdatedEvents(result);

		if (orderUpdatedEvents.isEmpty()) {
			return null;
		}
		OrderUpdatedEventResponse orderUpdatedEventResponse = orderUpdatedEvents.get(0);
		long longValue = orderUpdatedEventResponse.orderId.getValue().longValue();
		return getOrder(longValue);
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
		return DataParser.parseData(list);
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
		return DataParser.parseData(list);
	}

	@Required
	public void setSignMessageValidator(CertSignMessageValidator signMessageValidator) {
		this.signMessageValidator = signMessageValidator;
	}

}
