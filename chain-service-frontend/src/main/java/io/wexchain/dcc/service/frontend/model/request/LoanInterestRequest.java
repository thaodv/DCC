package io.wexchain.dcc.service.frontend.model.request;



import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class LoanInterestRequest {

	@NotNull(message = "借款产品订单不能为空")
	private Long productId;

	@NotNull(message = "借款金额不能为空")
	private BigDecimal amount;

	@NotNull(message = "借款周期不能为空")
	private BigDecimal loanPeriodValue;

	public BigDecimal getLoanPeriodValue() {
		return loanPeriodValue;
	}

	public void setLoanPeriodValue(BigDecimal loanPeriodValue) {
		this.loanPeriodValue = loanPeriodValue;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
