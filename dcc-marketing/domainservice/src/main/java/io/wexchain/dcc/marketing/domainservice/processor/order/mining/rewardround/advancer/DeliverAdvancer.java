package io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardround.advancer;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.executor.intf.OrderExecutor;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;

import io.wexchain.dcc.marketing.api.constant.MiningRewardRoundStatus;
import io.wexchain.dcc.marketing.common.constant.MiningRewardRoundItemStatus;
import io.wexchain.dcc.marketing.domain.MiningRewardRound;
import io.wexchain.dcc.marketing.domain.MiningRewardRoundItem;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardround.MiningRewardRoundInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardround.MiningRewardRoundTrigger;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.MiningRewardRoundItemInstruction;
import io.wexchain.dcc.marketing.repository.MiningRewardRoundItemRepository;


public class DeliverAdvancer  extends AbstractAdvancer<MiningRewardRound, MiningRewardRoundInstruction, MiningRewardRoundTrigger> {

	{
		availableStatus = MiningRewardRoundStatus.ANALYZED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MiningRewardRoundItemRepository miningRewardRoundItemRepository;

	@Resource(name = "miningRewardRoundItemExecutor")
	private OrderExecutor<MiningRewardRoundItem, MiningRewardRoundItemInstruction> miningRewardRoundItemExecutor;

	@Override
	public AdvancedResult<MiningRewardRound, MiningRewardRoundTrigger> advance(
			MiningRewardRound rewardRound, MiningRewardRoundInstruction instruction, Object message) {
		int count = miningRewardRoundItemRepository.countByMiningRewardRoundIdAndStatus(
				rewardRound.getId(), MiningRewardRoundItemStatus.SNAPSHOTED);
		logger.info("Waiting deliver item count: {}", count);
		if (count > 0) {
			miningRewardRoundItemRepository.findByMiningRewardRoundIdAndStatusOrderByScoreSnapshotDesc(
					rewardRound.getId(), MiningRewardRoundItemStatus.SNAPSHOTED)
					.forEach(item -> miningRewardRoundItemExecutor.execute(item, null, null));
		}
		Validate.isTrue(miningRewardRoundItemRepository.countByMiningRewardRoundIdAndStatus(
				rewardRound.getId(), MiningRewardRoundItemStatus.SNAPSHOTED) == 0);
		return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRoundTrigger.DELIVER));
	}

}