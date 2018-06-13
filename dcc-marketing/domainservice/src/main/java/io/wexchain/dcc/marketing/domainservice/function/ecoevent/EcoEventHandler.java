package io.wexchain.dcc.marketing.domainservice.function.ecoevent;

import io.wexchain.dcc.marketing.domain.RewardActionRecord;
import org.web3j.protocol.core.methods.response.Log;

import java.util.List;

/**
 * EcoEventHandler
 *
 * @author zhengpeng
 */
public interface EcoEventHandler {

    boolean canHandle(Log log);

    List<RewardActionRecord> handle(Log log);

}
