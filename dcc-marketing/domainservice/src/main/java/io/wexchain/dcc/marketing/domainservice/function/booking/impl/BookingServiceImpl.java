package io.wexchain.dcc.marketing.domainservice.function.booking.impl;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.rpc.RequestIdentity;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.cryptoasset.account.api.model.Account;
import io.wexchain.cryptoasset.account.api.model.AccountIndex;
import io.wexchain.cryptoasset.account.api.model.AccountTransaction;
import io.wexchain.cryptoasset.account.proxy.utils.MonoAccountingEntry;
import io.wexchain.cryptoasset.account.proxy.utils.MonoAccountingRequest;
import io.wexchain.cryptoasset.account.proxy.utils.MonoAccoutingDirection;
import io.wexchain.cryptoasset.account.proxy.utils.MonoAccoutingProxy;
import io.wexchain.cryptoasset.account.proxy.utils.OpenMonoAccountRequest;
import io.wexchain.dcc.marketing.domainservice.function.booking.BookingService;
import io.wexchain.dcc.marketing.domainservice.function.validator.Code2Exception;

/**
 * BookingServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private MonoAccoutingProxy monoAccoutingProxy;

    private final static String SET_OF_BOOKS_CODE = "BITEXPRESS_MINING_SCORE";
    private final static String SET_SOURCE_CODE = "DCC-MARKETING";

    @Override
    public Account openAccount(String code) {
        OpenMonoAccountRequest omar = new OpenMonoAccountRequest();
        omar.setCode(code);
        omar.setSetOfBooksCode(SET_OF_BOOKS_CODE);
        return Code2Exception.handleResultResponse(monoAccoutingProxy.openAccount(omar));
    }

    @Override
    public Account getAccountByCode(String code) {
        ResultResponse<Account> accountResultResponse = monoAccoutingProxy.getAccountByIndex(
                new AccountIndex(SET_OF_BOOKS_CODE, code));
        return Code2Exception.handleResultResponse(accountResultResponse);
    }

    @Override
    public AccountTransaction accounting(String code, String requestNo, BigDecimal amount) {
        MonoAccountingEntry mae = new MonoAccountingEntry();
        mae.setAccountCode(code);
        mae.setDirection(MonoAccoutingDirection.ADD);
        mae.setAmount(amount);
        mae.setDelay(true);

        MonoAccountingRequest mar = new MonoAccountingRequest();
        mar.setEntries(Collections.singletonList(mae));
        mar.setRequestIdentity(new RequestIdentity(SET_SOURCE_CODE, requestNo));
        mar.setSetOfBooksCode(SET_OF_BOOKS_CODE);
        ResultResponse<AccountTransaction> submit = monoAccoutingProxy.submit(mar);
        return Code2Exception.handleResultResponse(submit);
    }

    @Override
    public AccountTransaction add(String code, String requestNo, BigDecimal amount) {
        MonoAccountingEntry mae = new MonoAccountingEntry();
        mae.setAccountCode(code);
        mae.setDirection(MonoAccoutingDirection.ADD);
        mae.setAmount(amount);
        mae.setDelay(false);

        MonoAccountingRequest mar = new MonoAccountingRequest();
        mar.setEntries(Collections.singletonList(mae));
        mar.setRequestIdentity(new RequestIdentity(SET_SOURCE_CODE, requestNo));
        mar.setSetOfBooksCode(SET_OF_BOOKS_CODE);
        ResultResponse<AccountTransaction> submit = monoAccoutingProxy.submit(mar);
        return Code2Exception.handleResultResponse(submit);
    }

    @Override
    public AccountTransaction subtract(String code, String requestNo, BigDecimal amount) {
        MonoAccountingEntry mae = new MonoAccountingEntry();
        mae.setAccountCode(code);
        mae.setDirection(MonoAccoutingDirection.SUBSTRACT);
        mae.setAmount(amount);
        mae.setDelay(false);

        MonoAccountingRequest mar = new MonoAccountingRequest();
        mar.setEntries(Collections.singletonList(mae));
        mar.setRequestIdentity(new RequestIdentity(SET_SOURCE_CODE, requestNo));
        mar.setSetOfBooksCode(SET_OF_BOOKS_CODE);
        ResultResponse<AccountTransaction> submit = monoAccoutingProxy.submit(mar);
        return Code2Exception.handleResultResponse(submit);
    }

}
