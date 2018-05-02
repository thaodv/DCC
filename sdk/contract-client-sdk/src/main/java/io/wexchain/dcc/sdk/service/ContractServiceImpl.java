package io.wexchain.dcc.sdk.service;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.utils.Numeric;

import io.wexchain.dcc.sdk.client.constants.TxContstant;
import io.wexchain.dcc.sdk.client.contract.ContractClient;
import io.wexchain.dcc.sdk.client.receipt.ReceiptClient;
import io.wexchain.dcc.sdk.client.receipt.ReceiptResult;
import io.wexchain.dcc.sdk.client.ticket.TicketClient;

public class ContractServiceImpl<C extends ContractClient> {

	private static final Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);
	public static final Duration DEFAULT_WAIT_DURATION = Duration.ofSeconds(2);

	public static final int DEFAULT_ROUND_ROBIN_TIMES = 5;

	protected TicketClient ticketClient;

	protected ReceiptClient receiptClient;

	protected Duration waitDuration = DEFAULT_WAIT_DURATION;

	protected int roundRobinTimes = DEFAULT_ROUND_ROBIN_TIMES;

	protected C contractClient;

	public <T> UploadResult<T> upload(@NotBlank String address, @NotBlank String signMessage,
			@NotNull Function<UploadParam, String> uploaderFunction, @NotNull Function<String, T> eventExtractor)
			throws IOException, InterruptedException {
		String ticket = ticketClient.getTicket();
		String txHash = uploaderFunction.apply(new UploadParam(ticket, signMessage));
		return checkResult(txHash, address, eventExtractor);
	}

	public <T> UploadResult<T> checkResult(String txHash, String address, @NotNull Function<String, T> eventExtractor)
			throws InterruptedException, IOException {
		ReceiptResult receiptResult = null;
		for (int i = 0; i < roundRobinTimes; i++) {
			Thread.sleep(waitDuration.toMillis());
			receiptResult = receiptClient.gasReceiptResult(txHash, address);
			if (receiptResult != null && receiptResult.isHasReceipt()) {
				break;
			}
		}
		if (receiptResult == null) {
			return new UploadResult<T>(txHash, null, Optional.empty());
		}
		if (!receiptResult.isHasReceipt() || receiptResult.getApproximatelySuccess() == null
				|| !receiptResult.getApproximatelySuccess()) {
			return new UploadResult<T>(txHash, receiptResult, Optional.empty());
		}

		return new UploadResult<T>(txHash, receiptResult, Optional.ofNullable(eventExtractor.apply(txHash)));
	}

	protected String createSignMessage(Credentials credentials, Function<String, BigInteger> nonceFunction,
			Supplier<org.web3j.abi.datatypes.Function> functionSupplier, Supplier<String> contractAddressSupplier)
			throws IOException, CipherException {
		String data = FunctionEncoder.encode(functionSupplier.get());
		logger.trace("upload data:{}", data);
		RawTransaction rawTransaction = RawTransaction.createTransaction(nonceFunction.apply(credentials.getAddress()),
				TxContstant.DEFAULT_GAS_PRICE, TxContstant.DEFAULT_GAS_LIMIT, contractAddressSupplier.get(),
				TxContstant.DEFAULT_TX_VALUE, data);
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		return Numeric.toHexString(signedMessage);
	}

	public void setTicketClient(TicketClient ticketClient) {
		this.ticketClient = ticketClient;
	}

	public void setReceiptClient(ReceiptClient receiptClient) {
		this.receiptClient = receiptClient;
	}

	public void setWaitDuration(Duration waitDuration) {
		this.waitDuration = waitDuration;
	}

	public UploadResult<Void> fastFail(Credentials credentials,
			java.util.function.Function<String, BigInteger> nonceFunction)
			throws IOException, CipherException, InterruptedException {
		String signMessage = createSignMessage(credentials, nonceFunction, this::createFastFailFunction,
				contractClient::getContractAddress);
		return upload(credentials.getAddress(), signMessage, contractClient::fastFail, null);
	}

	private org.web3j.abi.datatypes.Function createFastFailFunction() {
		@SuppressWarnings("rawtypes")
		org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function("fastFail",
				Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
		return function;
	}

	public void setRoundRobinTimes(int roundRobinTimes) {
		this.roundRobinTimes = roundRobinTimes;
	}

	public void setContractClient(C contractClient) {
		this.contractClient = contractClient;
	}

}
