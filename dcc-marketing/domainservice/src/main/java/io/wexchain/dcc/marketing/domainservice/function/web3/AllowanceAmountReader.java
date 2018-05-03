package io.wexchain.dcc.marketing.domainservice.function.web3;

import java.math.BigInteger;

/**
 * AllowanceAmountReader
 *
 * @author zhengpeng
 */
public interface AllowanceAmountReader {

    BigInteger getAllowanceAmount(String address);

}
