package io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.executor.intf.OrderExecutor;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.dcc.marketing.api.constant.RewardDeliveryStatus;
import io.wexchain.dcc.marketing.api.constant.RewardRoundStatus;
import io.wexchain.dcc.marketing.domain.RewardDelivery;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewarddelivery.RewardDeliveryInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundTrigger;
import io.wexchain.dcc.marketing.repository.RewardDeliveryRepository;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


public class DeliverAdvancer extends AbstractAdvancer<RewardRound, RewardRoundInstruction, RewardRoundTrigger> {

	{
		availableStatus = RewardRoundStatus.ANALYZED;
	}

	@Autowired
	private RewardDeliveryRepository rewardDeliveryRepository;

	@Resource(name = "rewardDeliveryExecutor")
	private OrderExecutor<RewardDelivery, RewardDeliveryInstruction> rewardDeliveryExecutor;

	@Override
	public AdvancedResult<RewardRound, RewardRoundTrigger> advance(
			RewardRound rewardRound, RewardRoundInstruction instruction, Object message) {

		// 发放奖励
		List<Future<?>> futureList = new ArrayList<>();
		rewardDeliveryRepository.findByRewardRoundId(rewardRound.getId()).forEach(delivery ->
				futureList.add(rewardDeliveryExecutor.executeAsync(delivery, null, null)));

		while (!futureList.stream().allMatch(Future::isDone)) {
			try {
				Thread.sleep(60 * 1000L);
			} catch (InterruptedException e) {
				throw new ContextedRuntimeException(e);
			}
		}

		// 确认发放完毕
		if (rewardDeliveryRepository.countByRewardRoundIdAndStatus(
				rewardRound.getId(), RewardDeliveryStatus.CREATED) == 0) {
			return new AdvancedResult<>(new TriggerBehavior<>(RewardRoundTrigger.DELIVER));
		}

		return null;
	}
}