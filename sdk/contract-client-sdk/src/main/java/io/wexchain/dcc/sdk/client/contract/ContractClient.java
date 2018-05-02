package io.wexchain.dcc.sdk.client.contract;

import io.wexchain.dcc.sdk.service.UploadParam;

public interface ContractClient {
	String getContractAddress();

	String getOwner();

	String fastFail(UploadParam uploadParam);
}
