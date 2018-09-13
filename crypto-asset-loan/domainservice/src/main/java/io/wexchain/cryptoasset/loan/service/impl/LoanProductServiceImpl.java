package io.wexchain.cryptoasset.loan.service.impl;

import com.google.common.base.Suppliers;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.api.product.Currency;
import io.wexchain.cryptoasset.loan.api.product.Lender;
import io.wexchain.cryptoasset.loan.api.product.LoanProduct;
import io.wexchain.cryptoasset.loan.ext.integration.configuration.LoanProductConfiguration;
import io.wexchain.cryptoasset.loan.service.LoanProductService;
import io.wexchain.dcc.loan.sdk.service.LoanService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@Service
public class LoanProductServiceImpl implements LoanProductService {

	public static final BigInteger MIN_FEE_DENOMINATOR = new BigInteger("1000000000000000000");

	public static final BigInteger MIN_FEE_SCOPE = new BigInteger("10");

	@Autowired
	private LoanProductConfiguration loanProductConfiguration;

	@Autowired
	private LoanService loanService;

	private Supplier<List<BigInteger>> getDccFeeScope = Suppliers.memoizeWithExpiration(
			() -> {
				BigInteger minFee = loanService.getMinFee().divide(MIN_FEE_DENOMINATOR);
				BigInteger maxFee = minFee.add(MIN_FEE_SCOPE);
				List<BigInteger> dccFeeScope = new ArrayList<>();
				dccFeeScope.add(minFee);
				dccFeeScope.add(maxFee);
				return dccFeeScope;
			}, 1,
			TimeUnit.HOURS);


	@Override
	public Currency getCurrency(String symbol) {
		Currency currency = ErrorCodeValidate.notNull(loanProductConfiguration.currencyMap.get(symbol), CalErrorCode.CURRENCY_NOT_EXIST);
		return currency;
	}

	@Override
	public Lender getLenderByCode(String code) {
		Lender lender = ErrorCodeValidate.notNull(loanProductConfiguration.lenderMap.get(code), CalErrorCode.LENDER_NOT_EXIST);
		return lender;
	}

	@Override
	public Lender getDefaultLender() {
		return loanProductConfiguration.defaultLender;
	}

	@Override
	public List<Lender> queryLenderList() {
		return loanProductConfiguration.lenderConfigReader.getConfigList();
	}

	@Override
	public List<LoanProduct> getLoanProductByLenderCode(String lenderCode) {
		List<LoanProduct> loanProducts = ErrorCodeValidate.notNull(loanProductConfiguration.productWithLenderCodeMap.get(lenderCode), CalErrorCode.LOAN_PRODUCT_NOT_EXIST);
		if(CollectionUtils.isNotEmpty(loanProducts)){
			loanProducts.forEach(loanProduct -> loanProduct.setDccFeeScope(getDccFeeScope.get())
			);
		}
		return loanProducts;
	}

	@Override
	public LoanProduct getLoanProductById(Long id) {
		return ErrorCodeValidate.notNull(loanProductConfiguration.productWithIdMap.get(id), CalErrorCode.LOAN_PRODUCT_NOT_EXIST);
	}

	@Override
	public LoanProduct getLoanProductByCurrencySymbol(String currencySymbol) {
		Optional<Map.Entry<Long, LoanProduct>> first = loanProductConfiguration.productWithIdMap.entrySet()
				.stream().filter(entry -> entry.getValue().getCurrencySymbol().equalsIgnoreCase(currencySymbol))
				.findFirst();
		Map.Entry<Long, LoanProduct> entry = first.orElseThrow(
				() -> new ErrorCodeException(
				CalErrorCode.LOAN_PRODUCT_NOT_EXIST.name(),
				CalErrorCode.LOAN_PRODUCT_NOT_EXIST.getDescription()));
		return entry.getValue();
	}
}
