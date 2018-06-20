package io.wexchain.dcc.marketing.domainservice.function.booking;

import io.wexchain.cryptoasset.account.api.model.Account;
import io.wexchain.cryptoasset.account.api.model.AccountTransaction;

import java.math.BigDecimal;

/**
 * BookingService
 *
 * @author zhengpeng
 */
public interface BookingService {

    Account openAccount(String code);

    Account getAccountByCode(String code);

    AccountTransaction accounting(String code, String requestNo, BigDecimal amount);

}
