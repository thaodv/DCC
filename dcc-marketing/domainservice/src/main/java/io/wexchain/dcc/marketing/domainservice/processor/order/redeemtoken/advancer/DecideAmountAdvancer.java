package io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.dcc.marketing.api.constant.RedeemTokenStatus;
import io.wexchain.dcc.marketing.domain.BonusSlot;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenTrigger;
import io.wexchain.dcc.marketing.repository.BonusSlotRepository;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class DecideAmountAdvancer extends AbstractAdvancer<RedeemToken, RedeemTokenInstruction, RedeemTokenTrigger> {

	{
		availableStatus = RedeemTokenStatus.CREATED;
	}

	@Autowired
	private BonusSlotRepository bonusSlotRepository;

	@Override
	public AdvancedResult<RedeemToken, RedeemTokenTrigger> advance(
			RedeemToken redeemToken, RedeemTokenInstruction instruction, Object message) {
		if (redeemToken.getAmount() != null) {
			return new AdvancedResult<>(new TriggerBehavior<>(RedeemTokenTrigger.DECIDE), NextOperation.ASYNC_ADVANCE);
		}

		return new AdvancedResult<>(new TriggerBehavior<>(RedeemTokenTrigger.DECIDE, rt -> {
			List<BonusSlot> slotList = bonusSlotRepository.findByScenarioIdOrderByRankAsc(redeemToken.getScenario().getId());
			int currentCountSum = slotList.stream().mapToInt(BonusSlot::getCurrentCount).sum();
			int random = RandomUtils.nextInt(0, currentCountSum);
			slotList = putRank(slotList);
			BonusSlot decided = decideSlot(random, slotList);
			decided.setCurrentCount(decided.getCurrentCount() - 1);
			bonusSlotRepository.saveAll(slotList);
			rt.setAmount(decided.getAmount());
		}), NextOperation.ASYNC_ADVANCE);
	}

	private BonusSlot decideSlot(int random, List<BonusSlot> slotList) {
		for (BonusSlot bonusSlot : slotList) {
			if (random < bonusSlot.getRank()) {
				return bonusSlot;
			}
		}
		return null;
	}

	public List<BonusSlot> putRank(List<BonusSlot> list) {
		for (int i = 0; i < list.size(); i++) {
			BonusSlot bonusSlot = list.get(0);
			if (i == 0) {
				bonusSlot.setRank(bonusSlot.getCurrentCount());
				continue;
			}
			bonusSlot.setRank(bonusSlot.getCurrentCount() + list.get(i - 1).getRank());
		}
		return list;
	}
}