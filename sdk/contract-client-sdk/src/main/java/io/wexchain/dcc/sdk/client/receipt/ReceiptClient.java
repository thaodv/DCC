package io.wexchain.dcc.sdk.client.receipt;

import java.io.IOException;

public interface ReceiptClient {
	boolean hasReceipt(String transactionHash, String emitter) throws IOException;

	boolean hasReceipt(String transactionHash) throws IOException;

	ReceiptResult gasReceiptResult(String transactionHash, String emitter) throws IOException;

}
