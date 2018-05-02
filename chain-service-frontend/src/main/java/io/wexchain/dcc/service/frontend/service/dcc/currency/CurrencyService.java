package io.wexchain.dcc.service.frontend.service.dcc.currency;


import io.wexchain.dcc.service.frontend.model.Currency;

/**
 * @author yanyi
 */

public interface CurrencyService {

    Currency getCurrency(String symbol);

}
