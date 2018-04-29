package io.wexchain.dcc.sdk.client.block;

import java.io.IOException;
import java.util.List;

public interface BlockClient {

	List<String> getTxHashList(Long blockNumber) throws IOException;

	Long getBlockNumber() throws IOException;

}
