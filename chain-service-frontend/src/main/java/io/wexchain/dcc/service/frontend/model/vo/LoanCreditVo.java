package io.wexchain.dcc.service.frontend.model.vo;


import com.wexyun.open.api.enums.DurationType;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.model.Lender;

import java.math.BigDecimal;
import java.util.Date;

public class LoanCreditVo {
	private Long orderId;
	private String borrower;
	private OrderStatus status;
	private Long fee;
	private String receiveAddress;

	private Long loanProductId;

	private Currency currency;

	private BigDecimal loanRate;

	private Lender lender;

	private BigDecimal repayAheadRate;

	/**
	 * 申请ID
	 */
	private String              applyId;
	/**
	 * 借款金额
	 */
	private BigDecimal borrowAmount;
	/**
	 * 借款期限
	 */
	private Integer             borrowDuration;
	/**
	 * 借款期限单位
	 */
	private DurationType durationType;
	/**
	 * 申请日期
	 */
	private Date applyDate;
	/**
	 * 创建时间
	 */
	private Date                gmtCreate;
	private Date                expirationTime;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getBorrower() {
		return borrower;
	}

	public void setBorrower(String borrower) {
		this.borrower = borrower;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Long getFee() {
		return fee;
	}

	public void setFee(Long fee) {
		this.fee = fee;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public Long getLoanProductId() {
		return loanProductId;
	}

	public void setLoanProductId(Long loanProductId) {
		this.loanProductId = loanProductId;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getLoanRate() {
		return loanRate;
	}

	public void setLoanRate(BigDecimal loanRate) {
		this.loanRate = loanRate;
	}

	public Lender getLender() {
		return lender;
	}

	public void setLender(Lender lender) {
		this.lender = lender;
	}

	public BigDecimal getRepayAheadRate() {
		return repayAheadRate;
	}

	public void setRepayAheadRate(BigDecimal repayAheadRate) {
		this.repayAheadRate = repayAheadRate;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}

	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}

	public Integer getBorrowDuration() {
		return borrowDuration;
	}

	public void setBorrowDuration(Integer borrowDuration) {
		this.borrowDuration = borrowDuration;
	}

	public DurationType getDurationType() {
		return durationType;
	}

	public void setDurationType(DurationType durationType) {
		this.durationType = durationType;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
}
