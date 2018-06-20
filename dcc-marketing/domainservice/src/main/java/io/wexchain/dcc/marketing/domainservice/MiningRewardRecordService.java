package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * MiningRewardRecordService
 *
 * @author fu qiliang
 */
public interface MiningRewardRecordService {

	/** 保存挖矿订单 */
	void saveMiningRewardFromEvent(MiningRewardRecord record);

    Page<MiningRewardRecord> queryMiningRewardRecordPage(QueryMiningRewardRecordPageRequest request);

	List<EcoRewardRule> queryRewardRule(QueryRewardRuleRequest request);
}
