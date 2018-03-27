package io.wexchain.passport.gateway.service.loan.xiaoxinyong;

import com.godmonth.eth.rlp.web3j.SignMessageParser;
import io.wexchain.juzix.contract.loan.xiaoxinyong.DataParserV1;
import io.wexchain.juzix.contract.loan.xiaoxinyong.LoanOrderV1;
import io.wexchain.juzix.contract.loan.xiaoxinyong.LoanServiceV1;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoanServiceProxyJuzixImpl extends ContractProxyJuzixImpl<LoanServiceV1>
		implements LoanServiceProxy {

	private LoanSignMessageValidator signMessageValidator;

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
	public String approve(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateApprove(parseFull, System.currentTimeMillis());
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
	public String deposit(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateDeposit(parseFull, System.currentTimeMillis());
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
	public String recordRepay(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateRecordRepay(parseFull, System.currentTimeMillis());
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
	public String payOff(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validatePayOff(parseFull, System.currentTimeMillis());

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
	public String forceUpdateStatus(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateForceUpdateStatus(parseFull, System.currentTimeMillis());

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
	public LoanOrderV1 getOrder(Long orderId) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getOrder(new Uint256(BigInteger.valueOf(orderId))).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return DataParserV1.parseLoanOrder(list, orderId);
	}

	@Override
	public LoanOrderV1 getOrder(String txHash) {
		EthGetTransactionReceipt ethGetTransactionReceipt = null;
		try {
			ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(txHash).sendAsync().get(readTimeoutSecond,
					TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		TransactionReceipt result = ethGetTransactionReceipt.getResult();
		if (result == null) {
			return null;
		}

		List<LoanServiceV1.OrderUpdatedEventResponse> orderUpdatedEvents = contract.getOrderUpdatedEvents(result);

		if (orderUpdatedEvents.isEmpty()) {
			return null;
		}
		LoanServiceV1.OrderUpdatedEventResponse orderUpdatedEventResponse = orderUpdatedEvents.get(0);
		long longValue = orderUpdatedEventResponse.orderId.getValue().longValue();
		return getOrder(longValue);
	}

	@Required
	public void setSignMessageValidator(LoanSignMessageValidator signMessageValidator) {
		this.signMessageValidator = signMessageValidator;
	}

}
