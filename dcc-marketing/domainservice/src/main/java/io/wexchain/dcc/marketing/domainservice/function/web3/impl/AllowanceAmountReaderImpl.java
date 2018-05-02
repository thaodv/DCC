package io.wexchain.dcc.marketing.domainservice.function.web3.impl;

import io.wexchain.dcc.marketing.domainservice.function.web3.AllowanceAmountReader;
import io.wexchain.dcc.marketing.ext.integration.web3.TestToken;
import juzix.web3j.NonceRawTransactionManager;
import juzix.web3j.protocol.CustomWeb3j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

/**
 * AllowanceAmountReaderImpl
 *
 * @author zhengpeng
 */
@Service
public class AllowanceAmountReaderImpl implements AllowanceAmountReader {

    @Autowired
    private CustomWeb3j web3j;

    @Autowired
    @Qualifier("marketingCredentials")
    private Credentials credentials;

    private static BigInteger GAS_LIMIT = new BigInteger("9999999999999");
    private static BigInteger GAS_PRICE = new BigInteger("2100000000000");

    @Override
    public BigInteger getAllowanceAmount(String address) {
        try {
            TestToken dccToken = TestToken.load(
                    "0x625769a40adcd290591cd4a83eae25d374099d4d", web3j,
                    new NonceRawTransactionManager(web3j, credentials),
                    GAS_PRICE, GAS_LIMIT);
            Uint256 uint256 = dccToken.allowance(new Address(address), new Address(credentials.getAddress())).get();
            return uint256.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
