package io.wexchain.passport.gateway.service.contract;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ReceiptResult {
	private boolean hasReceipt;

	/**
	 * true when gasUsed less than gas, otherwise false.
	 */
	private Boolean approximatelySuccess;

	public ReceiptResult() {
		super();
	}

	public ReceiptResult(boolean hasReceipt, Boolean approximatelySuccess) {
		super();
		this.hasReceipt = hasReceipt;
		this.approximatelySuccess = approximatelySuccess;
	}

	public boolean isHasReceipt() {
		return hasReceipt;
	}

	public void setHasReceipt(boolean hasReceipt) {
		this.hasReceipt = hasReceipt;
	}

	public Boolean getApproximatelySuccess() {
		return approximatelySuccess;
	}

	public void setApproximatelySuccess(Boolean approximatelySuccess) {
		this.approximatelySuccess = approximatelySuccess;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("approximatelySuccess", this.approximatelySuccess).append("hasReceipt", this.hasReceipt)
				.toString();
	}

}
