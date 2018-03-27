package io.wexchain.passport.gateway.service.ca;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import com.godmonth.eth.rlp.web3j.SignMessageParser;

import io.wexchain.juzix.contract.passport.Ca;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;

public class CaRepoJuzixImpl extends ContractProxyJuzixImpl<Ca> implements CaRepo {

	@Autowired
	private CaSignMessageValidator signMessageValidator;

	@Override
	public String put(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validatePut(parseFull, System.currentTimeMillis());

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
	public String delete(String signMessageHex) {
		Pair<RawTransaction, SignatureData> parseFull = SignMessageParser.parseFull(signMessageHex);
		signMessageValidator.validateDelete(parseFull, System.currentTimeMillis());

		try {
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signMessageHex).sendAsync()
					.get(writeTimeoutSecond, TimeUnit.SECONDS);
			return ethSendTransaction.getResult();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	@Override
	public byte[] get(String address) {
		try {
			return contract.getKey(new Address(address)).get(readTimeoutSecond, TimeUnit.SECONDS).getValue();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
	}

}
