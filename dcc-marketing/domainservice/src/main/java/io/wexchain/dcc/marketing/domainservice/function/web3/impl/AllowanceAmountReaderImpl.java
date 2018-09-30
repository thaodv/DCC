package io.wexchain.dcc.marketing.domainservice.function.web3.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.web3.AllowanceAmountReader;

/**
 * AllowanceAmountReaderImpl
 *
 * @author zhengpeng
 */
@Service
public class AllowanceAmountReaderImpl implements AllowanceAmountReader {

    @Autowired
    private CahFunction cahFunction;

    @Override
    public BigInteger getAllowanceAmount(String address) {
        return cahFunction.getBalance(address, "DCC_JUZIX");
    }
}
