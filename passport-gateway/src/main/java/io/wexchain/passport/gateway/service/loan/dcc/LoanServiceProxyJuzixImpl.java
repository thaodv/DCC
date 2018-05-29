package io.wexchain.passport.gateway.service.loan.dcc;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

import io.wexchain.juzix.contract.commons.EventParserFix;
import io.wexchain.juzix.contract.loan.dcc.DataParser;
import io.wexchain.juzix.contract.loan.dcc.LoanOrder;
import io.wexchain.juzix.contract.loan.dcc.LoanOrderUpdatedEvent;
import io.wexchain.juzix.contract.loan.dcc.LoanService;
import io.wexchain.passport.gateway.ctrlr.loan.dcc.QueryOrderByAddress;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;

public class LoanServiceProxyJuzixImpl extends ContractProxyJuzixImpl<LoanService> implements LoanServiceProxy {

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
	public String cancel(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateCancel(parseFull, System.currentTimeMillis());
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
	public String audit(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateAudit(parseFull, System.currentTimeMillis());
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
	public String deliver(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateDeliver(parseFull, System.currentTimeMillis());
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
	public String deliverFailure(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateDeliverFailure(parseFull, System.currentTimeMillis());
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
	public String receive(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateReceive(parseFull, System.currentTimeMillis());
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
	public String confirmRepay(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateConfirmRepay(parseFull, System.currentTimeMillis());
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
	public String updateRepayDigest(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateUpdateRepayDigest(parseFull, System.currentTimeMillis());
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
	public LoanOrder getOrder(Long orderId) {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getOrder(new Uint256(BigInteger.valueOf(orderId))).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return DataParser.parseLoanOrder(list, orderId);
	}

	@Override
	public List<LoanOrder> getOrder(String txHash) {
		List<LoanOrderUpdatedEvent> orderUpdatedEvents = getOrderUpdatedEvents(txHash);
		return orderUpdatedEvents.stream().map(LoanOrderUpdatedEvent::getOrderId).map(this::getOrder)
				.collect(Collectors.toList());
	}

	@Override
	public BigInteger getOrderArrayLengthByBorrowerIndex(String address) {
		Uint256 length = null;
		try {
			length = contract.getOrderArrayLengthByBorrowerIndex(new Address(address)).get(readTimeoutSecond,
					TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return length.getValue();
	}

	public Page<BigInteger> queryOrderIdPageByBorrowIndex(QueryOrderByAddress queryOrderParam) {
		int length = getOrderArrayLengthByBorrowerIndex(queryOrderParam.getAddress()).intValueExact();
		List<Uint256> list = null;
		if (length > 0 || queryOrderParam.getSize() == 0) {
			int from = queryOrderParam.getNumber() * queryOrderParam.getSize();
			if (from < length) {
				int to = Math.min(length - 1, from + queryOrderParam.getSize());
				if (from <= to) {
					try {
						list = contract
								.queryOrderIdListByBorrowIndex(new Address(queryOrderParam.getAddress()),
										new Uint256(BigInteger.valueOf(from)), new Uint256(BigInteger.valueOf(to)))
								.get(readTimeoutSecond, TimeUnit.SECONDS).getValue();
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						throw new ContextedRuntimeException(e);
					}
				}
			}else {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
		}
		List<BigInteger> indexList = list.stream().map(Uint256::getValue).collect(Collectors.toList());
		return new PageImpl<BigInteger>(indexList,
				PageRequest.of(queryOrderParam.getNumber(), queryOrderParam.getSize()), length);
	}

	@Override
	public Page<LoanOrder> queryOrderPageByBorrowIndex(QueryOrderByAddress queryOrderParam) {
		Page<BigInteger> page = queryOrderIdPageByBorrowIndex(queryOrderParam);
		List<LoanOrder> collect = page.getContent().stream().map(BigInteger::longValueExact).map(this::getOrder)
				.collect(Collectors.toList());
		return new PageImpl<>(collect, page.getPageable(), page.getTotalElements());
	}

	@Override
	public BigInteger getMinFee() {
		Uint256 minFee = null;
		try {
			minFee = contract.getMinFee().get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		return minFee.getValue();
	}

	@Override
	public List<LoanOrderUpdatedEvent> getOrderUpdatedEvents(String txHash) {
		EthGetTransactionReceipt ethGetTransactionReceipt = null;
		try {
			ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(txHash).sendAsync().get(readTimeoutSecond,
					TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			throw new ContextedRuntimeException(e1);
		}
		TransactionReceipt result = ethGetTransactionReceipt.getResult();
		if (result == null) {
			return new ArrayList<LoanOrderUpdatedEvent>();
		}

		final Event event = new Event("orderUpdated", Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
		}, new TypeReference<Address>() {
		}), Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {
		}));

		List<EventValues> valueList = EventParserFix.extractEventParameters(event, result, getContractAddress());
		ArrayList<LoanService.OrderUpdatedEventResponse> responses = new ArrayList<LoanService.OrderUpdatedEventResponse>(
				valueList.size());
		for (EventValues eventValues : valueList) {
			LoanService.OrderUpdatedEventResponse typedResponse = new LoanService.OrderUpdatedEventResponse();
			typedResponse.id = (Uint256) eventValues.getIndexedValues().get(0);
			typedResponse.borrower = (Address) eventValues.getIndexedValues().get(1);
			typedResponse.status = (Uint8) eventValues.getNonIndexedValues().get(0);
			responses.add(typedResponse);
		}
		return responses.stream().map(DataParser::parseOrderUpdatedEventResponse).collect(Collectors.toList());
	}

	@Required
	public void setSignMessageValidator(LoanSignMessageValidator signMessageValidator) {
		this.signMessageValidator = signMessageValidator;
	}

}
