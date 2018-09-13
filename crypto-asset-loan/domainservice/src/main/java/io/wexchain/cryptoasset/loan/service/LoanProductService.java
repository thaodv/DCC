package io.wexchain.cryptoasset.loan.service;


import io.wexchain.cryptoasset.loan.api.product.Currency;
import io.wexchain.cryptoasset.loan.api.product.Lender;
import io.wexchain.cryptoasset.loan.api.product.LoanProduct;

import java.util.List;

public interface LoanProductService {

	Currency getCurrency(String symbol);

	Lender getLenderByCode(String code);

	Lender getDefaultLender();

	List<Lender> queryLenderList();

	List<LoanProduct> getLoanProductByLenderCode(String lenderCode);

	LoanProduct getLoanProductById(Long id);

	LoanProduct getLoanProductByCurrencySymbol(String currencySymbol);
}
