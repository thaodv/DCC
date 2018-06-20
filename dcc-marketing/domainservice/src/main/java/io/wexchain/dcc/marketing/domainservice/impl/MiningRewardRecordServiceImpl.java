package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.data.page.PageUtils;
import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRecordService;
import io.wexchain.dcc.marketing.domainservice.event.OrderUpdatedEvent;
import io.wexchain.dcc.marketing.domainservice.processor.order.miningrecordaction.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.EcoRewardRuleRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MiningRewardRecordServiceImpl
 *
 * @author fu qiliang
 */
@Service
public class MiningRewardRecordServiceImpl implements MiningRewardRecordService {
	@Resource(name = "miningRewardRecordExecutor")
	private OrderExecutor<MiningRewardRecord, MiningRewardRecordInstruction> miningRwdRecExecutor;

	@Autowired
	private MiningRewardRecordRepository miningRewardRecordRepository;

	@Autowired
	private EcoRewardRuleRepository ecoRewardRuleRepository;

	@Override
	public void saveMiningRewardFromEvent(MiningRewardRecord rewardRecord) {
		MiningRewardRecord mining = miningRewardRecordRepository.save(rewardRecord);
		miningRwdRecExecutor.executeAsync(mining, null, null);
	}

	@Override
	public Page<MiningRewardRecord> queryMiningRewardRecordPage(QueryMiningRewardRecordPageRequest request) {
		PageRequest pageRequest = PageUtils.convert(request.getSortPageParam());
		return miningRewardRecordRepository.findByAddress(request.getAddress(), pageRequest);
	}

	@Override
    public List<EcoRewardRule> queryRewardRule(QueryRewardRuleRequest request) {
		List<EcoRewardRule> list = ecoRewardRuleRepository.findByScenarioActivityCodeAndParticipatorRoleOrderByIdAsc(
				request.getActivityCode(), request.getParticipatorRole());
		return list.stream().filter(rule -> StringUtils.isNotEmpty(rule.getGroupCode())).collect(Collectors.toList());
    }

}
