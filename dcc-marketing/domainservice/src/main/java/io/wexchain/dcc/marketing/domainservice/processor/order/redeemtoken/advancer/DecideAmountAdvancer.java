package io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.advancer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;

import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.constant.RedeemTokenStatus;
import io.wexchain.dcc.marketing.domain.BonusSlot;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.redeemtoken.RedeemTokenTrigger;
import io.wexchain.dcc.marketing.repository.BonusSlotRepository;
import io.wexchain.dcc.marketing.repository.query.BonusSlotQueryBuilder;


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
			List<BonusSlot> slotList = bonusSlotRepository.findAll(BonusSlotQueryBuilder.query(
					redeemToken.getScenario().getId()), Sort.by(Sort.Order.asc("rank")));
			ErrorCodeValidate.isTrue(CollectionUtils.isNotEmpty(slotList), MarketingErrorCode.BONUS_IS_EMPTY);

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

	private List<BonusSlot> putRank(List<BonusSlot> list) {
		for (int i = 0; i < list.size(); i++) {
			BonusSlot bonusSlot = list.get(i);
			if (i == 0) {
				bonusSlot.setRank(bonusSlot.getCurrentCount());
				continue;
			}
			bonusSlot.setRank(bonusSlot.getCurrentCount() + list.get(i - 1).getRank());
		}
		return list;
	}
}