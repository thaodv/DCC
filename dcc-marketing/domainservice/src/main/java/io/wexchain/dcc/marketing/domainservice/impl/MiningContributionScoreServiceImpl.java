package io.wexchain.dcc.marketing.domainservice.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.cryptoasset.account.api.model.Account;
import io.wexchain.cryptoasset.account.api.model.AccountIndex;
import io.wexchain.cryptoasset.account.proxy.utils.MonoAccoutingProxy;
import io.wexchain.dcc.marketing.domainservice.MiningContributionScoreService;

@Service
public class MiningContributionScoreServiceImpl implements MiningContributionScoreService {

    @Autowired
    private MonoAccoutingProxy monoAccoutingProxy;

    private final static String SET_OF_BOOKS_CODE = "BITEXPRESS_MINING_SCORE";

    @Override
    public BigDecimal queryMiningContributionScore(String address) {
        ResultResponse<Account> accountResultResponse = monoAccoutingProxy.getAccountByIndex(
                new AccountIndex(SET_OF_BOOKS_CODE, address));
        if(accountResultResponse.getResult()!= null) {
            return accountResultResponse.getResult().getBalance();
        }
        return new BigDecimal(0);
    }

}
