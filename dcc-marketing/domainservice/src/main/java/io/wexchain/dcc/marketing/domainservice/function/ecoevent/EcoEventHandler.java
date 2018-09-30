package io.wexchain.dcc.marketing.domainservice.function.ecoevent;

import java.util.List;

import org.web3j.protocol.core.methods.response.Log;

import io.wexchain.dcc.marketing.domain.RewardActionRecord;

/**
 * EcoEventHandler
 *
 * @author zhengpeng
 */
public interface EcoEventHandler {

    boolean canHandle(Log log);

    List<RewardActionRecord> handle(Log log);

}
