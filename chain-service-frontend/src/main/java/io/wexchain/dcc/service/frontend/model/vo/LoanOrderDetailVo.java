package io.wexchain.dcc.service.frontend.model.vo;


import java.math.BigDecimal;
import java.util.Date;

public class LoanOrderDetailVo extends LoanOrderVo{

	private String durationUnit;

	private Integer borrowDuration;

	private BigDecimal fee;

	private Date deliverDate;

	private Date repayDate;

	private Date expectRepayDate;

	private BigDecimal loanInterest;

	private BigDecimal expectLoanInterest;

	private String receiverAddress;

	private Boolean earlyRepayAvailable = false;

	private Boolean allowRepayPermit;

	private BigDecimal loanRate;

	public BigDecimal getLoanRate() {
		return loanRate;
	}

	public void setLoanRate(BigDecimal loanRate) {
		this.loanRate = loanRate;
	}

	public Boolean getAllowRepayPermit() {
		return allowRepayPermit;
	}

	public void setAllowRepayPermit(Boolean allowRepayPermit) {
		this.allowRepayPermit = allowRepayPermit;
	}

	public Boolean getEarlyRepayAvailable() {
		return earlyRepayAvailable;
	}

	public void setEarlyRepayAvailable(Boolean earlyRepayAvailable) {
		this.earlyRepayAvailable = earlyRepayAvailable;
	}

	public BigDecimal getLoanInterest() {
		return loanInterest;
	}

	public void setLoanInterest(BigDecimal loanInterest) {
		this.loanInterest = loanInterest;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Date getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}

	public Date getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}

	public Date getExpectRepayDate() {
		return expectRepayDate;
	}

	public void setExpectRepayDate(Date expectRepayDate) {
		this.expectRepayDate = expectRepayDate;
	}

	public Integer getBorrowDuration() {
		return borrowDuration;
	}

	public void setBorrowDuration(Integer borrowDuration) {
		this.borrowDuration = borrowDuration;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public BigDecimal getExpectLoanInterest() {
		return expectLoanInterest;
	}

	public void setExpectLoanInterest(BigDecimal expectLoanInterest) {
		this.expectLoanInterest = expectLoanInterest;
	}
}
