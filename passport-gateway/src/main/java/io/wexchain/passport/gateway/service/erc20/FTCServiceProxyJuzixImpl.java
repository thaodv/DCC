package io.wexchain.passport.gateway.service.erc20;

import io.wexchain.juzix.contract.erc20.DataParser;
import io.wexchain.juzix.contract.erc20.FTCToken;
import io.wexchain.juzix.contract.erc20.FreeRate;
import io.wexchain.passport.gateway.service.contract.ContractProxyJuzixImpl;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FTCServiceProxyJuzixImpl extends ContractProxyJuzixImpl<FTCToken>
		implements FTCServiceProxy {

	@Override
	public String getContractAddress() {
		return contract.getContractAddress();
	}



	@Override
	public BigDecimal getFeeRate() {
		@SuppressWarnings("rawtypes")
		List<Type> list = null;
		try {
			list = contract.getFeeRate().get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		FreeRate freeRate = DataParser.parseFreeRate(list);
		if(freeRate != null){
			return new BigDecimal(freeRate.getFeeRateNumerator()).divide(new BigDecimal(freeRate.getFeeRateDenominator()));
		}
		return null;
	}

	@Override
	public BigInteger getExpectedFee(String sender, BigInteger amount) {
		Uint256 expectedFee = null;
		try {
			expectedFee = contract.getExpectedFee(new Address(sender), new Uint256(amount)).get(readTimeoutSecond, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new ContextedRuntimeException(e);
		}
		if(expectedFee != null){
			return expectedFee.getValue();
		}
		return null;
	}
}
