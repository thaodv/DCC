package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.domain.MiningRewardRecord;

/**
 * MiningRewardRecordService
 *
 * @author fu qiliang
 */
public interface MiningRewardRecordService {

	/** 保存挖矿订单 */
	void saveMiningRewardFromEvent(MiningRewardRecord record);

}
