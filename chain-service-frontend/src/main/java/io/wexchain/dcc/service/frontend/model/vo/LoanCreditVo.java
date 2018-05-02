package io.wexchain.dcc.service.frontend.model.vo;


import com.wexyun.open.api.domain.credit2.Credit2Apply;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.service.frontend.common.enums.CertType;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.model.Lender;
import io.wexchain.dcc.service.frontend.model.LoanProduct;
import io.wexchain.dcc.service.frontend.model.Period;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class LoanCreditVo {

	private LoanOrder order;

	private LoanProductVo loanProduct;

	private Credit2Apply credit2Apply;

	public LoanOrder getOrder() {
		return order;
	}

	public void setOrder(LoanOrder order) {
		this.order = order;
	}

	public LoanProductVo getLoanProduct() {
		return loanProduct;
	}

	public void setLoanProduct(LoanProductVo loanProduct) {
		this.loanProduct = loanProduct;
	}

	public Credit2Apply getCredit2Apply() {
		return credit2Apply;
	}

	public void setCredit2Apply(Credit2Apply credit2Apply) {
		this.credit2Apply = credit2Apply;
	}
}
