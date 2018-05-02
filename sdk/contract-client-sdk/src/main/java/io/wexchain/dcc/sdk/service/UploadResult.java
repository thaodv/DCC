package io.wexchain.dcc.sdk.service;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.wexchain.dcc.sdk.client.receipt.ReceiptResult;

public class UploadResult<T> {
	private String txHash;

	private ReceiptResult receiptResult;

	private Optional<T> events;

	public UploadResult(String txHash, ReceiptResult receiptResult, Optional<T> events) {
		this.txHash = txHash;
		this.receiptResult = receiptResult;
		this.events = events;
	}

	public ReceiptResult getReceiptResult() {
		return receiptResult;
	}

	public String getTxHash() {
		return txHash;
	}

	public Optional<T> checkEvents() throws NoReceiptException {
		if (!receiptResult.isHasReceipt()) {
			throw new NoReceiptException();
		}
		return events;
	}

	public Optional<T> getEvents() {
		return events;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("receiptResult", this.receiptResult)
				.append("txHash", this.txHash).append("events", this.events).toString();
	}

}
