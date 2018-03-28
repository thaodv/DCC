package io.wexchain.passport.gateway.service.erc20;

import io.wexchain.juzix.contract.erc20.FTCToken;
import io.wexchain.passport.gateway.service.contract.ContractProxy;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface FTCServiceProxy extends ContractProxy<FTCToken> {


	BigDecimal getFeeRate();

	BigInteger getExpectedFee(String sender, BigInteger amount);

}
