package io.wexchain.dcc.service.frontend.model.vo;


import io.wexchain.dcc.service.frontend.common.enums.CertType;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.model.Lender;
import io.wexchain.dcc.service.frontend.model.LoanProduct;
import io.wexchain.dcc.service.frontend.model.Period;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class LoanProductVo {

	private Long id;

	private Currency currency;

	private List<BigInteger> dccFeeScope;

	private String description;

	private List<BigInteger> volumeOptionList;

	private BigDecimal loanRate;

	private List<Period> loanPeriodList;

	private Lender lender;

	private List<CertType> requisiteCertList;

	private boolean repayPermit;

	private BigDecimal repayAheadRate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Lender getLender() {
		return lender;
	}

	public void setLender(Lender lender) {
		this.lender = lender;
	}

	public List<BigInteger> getDccFeeScope() {
		return dccFeeScope;
	}

	public void setDccFeeScope(List<BigInteger> dccFeeScope) {
		this.dccFeeScope = dccFeeScope;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<BigInteger> getVolumeOptionList() {
		return volumeOptionList;
	}

	public void setVolumeOptionList(List<BigInteger> volumeOptionList) {
		this.volumeOptionList = volumeOptionList;
	}

	public BigDecimal getLoanRate() {
		return loanRate;
	}

	public void setLoanRate(BigDecimal loanRate) {
		this.loanRate = loanRate;
	}

	public List<Period> getLoanPeriodList() {
		return loanPeriodList;
	}

	public void setLoanPeriodList(List<Period> loanPeriodList) {
		this.loanPeriodList = loanPeriodList;
	}


	public List<CertType> getRequisiteCertList() {
		return requisiteCertList;
	}

	public void setRequisiteCertList(List<CertType> requisiteCertList) {
		this.requisiteCertList = requisiteCertList;
	}

	public boolean isRepayPermit() {
		return repayPermit;
	}

	public void setRepayPermit(boolean repayPermit) {
		this.repayPermit = repayPermit;
	}

	public BigDecimal getRepayAheadRate() {
		return repayAheadRate;
	}

	public void setRepayAheadRate(BigDecimal repayAheadRate) {
		this.repayAheadRate = repayAheadRate;
	}

	public LoanProductVo() {
	}

	public LoanProductVo(LoanProduct loanProduct) {
		this.id = loanProduct.getId();
		this.dccFeeScope = loanProduct.getDccFeeScope();
		this.description = loanProduct.getDescription();
		this.volumeOptionList = loanProduct.getVolumeOptionList();
		this.loanRate = loanProduct.getLoanRate();
		this.loanPeriodList = loanProduct.getLoanPeriodList();
		this.requisiteCertList = loanProduct.getRequisiteCertList();
		this.repayPermit = loanProduct.isRepayPermit();
		this.repayAheadRate = loanProduct.getRepayAheadRate();

	}
}
