package io.wexchain.dcc.service.frontend.model.vo;


import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.model.Lender;

import java.math.BigDecimal;
import java.util.Date;

public class LoanOrderVo {

	private Long orderId;

	private LoanOrderStatus status;

	private Date applyDate;

	private Currency currency;

	private Lender lender;

	private BigDecimal amount;

	private String productLogoUrl;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public LoanOrderStatus getStatus() {
		return status;
	}

	public void setStatus(LoanOrderStatus status) {
		this.status = status;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
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

	public String getProductLogoUrl() {
		return productLogoUrl;
	}

	public void setProductLogoUrl(String productLogoUrl) {
		this.productLogoUrl = productLogoUrl;
	}
}
