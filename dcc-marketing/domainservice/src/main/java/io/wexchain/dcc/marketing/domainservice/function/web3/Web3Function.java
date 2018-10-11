package io.wexchain.dcc.marketing.domainservice.function.web3;

import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;

/**
 * Web3Function
 *
 * @author zhengpeng
 */
public interface Web3Function {

    List<EthBlock.Block> getBlockList(long startNumber, long endNumber);

    List<Log> getEventLogList(long startNumber, long endNumber);

    Long getBlockNumberAfterTime(Date time);

    @Cacheable(cacheNames = "CONTRACT_OWNER", key = "#contractAddress")
    String getContractOwner(String contractAddress);

}
