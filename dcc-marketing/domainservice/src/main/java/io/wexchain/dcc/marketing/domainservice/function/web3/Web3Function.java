package io.wexchain.dcc.marketing.domainservice.function.web3;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;

import java.util.Date;
import java.util.List;

/**
 * Web3Function
 *
 * @author zhengpeng
 */
public interface Web3Function {

    List<EthBlock.Block> getBlockList(long startNumber, long endNumber);

    List<Log> getEventLogList(long startNumber, long endNumber);

    Long getBlockNumberAfterTime(Date time);

}
