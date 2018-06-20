package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import io.wexchain.dcc.marketing.common.constant.MiningActionRecordStatus;
import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRecordService;
import io.wexchain.dcc.marketing.domainservice.event.OrderUpdatedEvent;
import io.wexchain.dcc.marketing.domainservice.processor.order.miningrecordaction.MiningRewardRecordInstruction;
import io.wexchain.dcc.marketing.repository.EcoRewardRuleRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

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
	private EcoRewardRuleRepository ecoRewardRuleRepository;

	@Override
	public void saveMiningRewardFromEvent(MiningRewardRecord rewardRecord) {
		MiningRewardRecord mining = miningRewardRecordRepository.save(rewardRecord);
		miningRwdRecExecutor.executeAsync(mining, null, null);
	}

}
