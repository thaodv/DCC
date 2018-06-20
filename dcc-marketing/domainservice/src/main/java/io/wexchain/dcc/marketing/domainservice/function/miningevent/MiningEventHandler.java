package io.wexchain.dcc.marketing.domainservice.function.miningevent;

import io.wexchain.dcc.marketing.domain.MiningRewardRecord;

/**
 * MiningEventHandler
 * 挖矿事件处理顶层接口
 *
 * @author fu qiliang
 */
public interface MiningEventHandler {

    /** 判断是否能处理事件 */
    boolean canHandle(Object obj);

    /** 处理挖矿记录 */
    MiningRewardRecord handle(Object obj);
}
