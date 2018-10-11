package io.wexchain.dcc.marketing.domainservice.function.booking;

import java.math.BigDecimal;

import io.wexchain.cryptoasset.account.api.model.Account;
import io.wexchain.cryptoasset.account.api.model.AccountTransaction;

/**
 * BookingService
 *
 * @author zhengpeng
 */
public interface BookingService {

    Account openAccount(String code);

    Account getAccountByCode(String code);

    AccountTransaction accounting(String code, String requestNo, BigDecimal amount);

    AccountTransaction add(String code, String requestNo, BigDecimal amount);

    AccountTransaction subtract(String code, String requestNo, BigDecimal amount);

}
