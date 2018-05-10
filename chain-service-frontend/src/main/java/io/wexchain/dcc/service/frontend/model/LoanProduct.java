package io.wexchain.dcc.service.frontend.model;


import io.wexchain.dcc.service.frontend.common.enums.CertType;
import org.web3j.abi.datatypes.Int;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class LoanProduct {

	private Long id;

	private String currencySymbol;

	private String loanType;

	private List<BigInteger> dccFeeScope;

	private String description;

	private List<BigInteger> volumeOptionList;

	private BigDecimal loanRate;

	private List<Period> loanPeriodList;

	private String lenderCode;

	private List<CertType> requisiteCertList;

	private boolean repayPermit;

	private BigDecimal repayAheadRate;

	private int repayCyclesNo;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public int getRepayCyclesNo() {
		return repayCyclesNo;
	}

	public void setRepayCyclesNo(int repayCyclesNo) {
		this.repayCyclesNo = repayCyclesNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
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

	public String getLenderCode() {
		return lenderCode;
	}

	public void setLenderCode(String lenderCode) {
		this.lenderCode = lenderCode;
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
}
