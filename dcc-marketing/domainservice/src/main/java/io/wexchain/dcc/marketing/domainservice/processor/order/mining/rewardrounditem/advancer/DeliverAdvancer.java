package io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.dcc.marketing.api.constant.CandyStatus;
import io.wexchain.dcc.marketing.common.constant.MiningRewardRoundItemStatus;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.Candy;
import io.wexchain.dcc.marketing.domain.MiningRewardRoundItem;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.MiningRewardRoundItemInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.MiningRewardRoundItemTrigger;
import io.wexchain.dcc.marketing.repository.CandyRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRoundItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DeliverAdvancer extends AbstractAdvancer<
		MiningRewardRoundItem, MiningRewardRoundItemInstruction, MiningRewardRoundItemTrigger> {

	{
		availableStatus = MiningRewardRoundItemStatus.SNAPSHOTED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CandyRepository candyRepository;

	@Autowired
	private MiningRewardRoundItemRepository miningRewardRoundItemRepository;

	@Value("${candy.num.max}")
	private Integer maxCandyNum = 16;

	@Value("${candy.amount.per.round}")
	private BigDecimal candyAmountPerRound;

	@Value("${candy.amount.max}")
	private BigDecimal maxCandyAmount;

	@Value("${candy.amount.min}")
	private BigDecimal minCandyAmount;

	private static final String MINING_REWARD_BOX_CODE = "BITEXPRESS_MINING_CANDY_BOX";

	@Override
	public AdvancedResult<MiningRewardRoundItem, MiningRewardRoundItemTrigger> advance(
			MiningRewardRoundItem rewardRoundItem, MiningRewardRoundItemInstruction instruction, Object message) {

		Integer candyNum = candyRepository.countByOwnerAndBoxCodeAndStatus(
				rewardRoundItem.getAddress(), MINING_REWARD_BOX_CODE, CandyStatus.CREATED);
		if (Integer.compare(candyNum, maxCandyNum) >= 0) {
			return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRoundItemTrigger.SKIP));
		}
		if (rewardRoundItem.getScoreSnapshot() == null ||
				rewardRoundItem.getScoreSnapshot().compareTo(BigDecimal.ZERO) == 0) {
			return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRoundItemTrigger.SKIP));
		}
		BigDecimal totalScore = miningRewardRoundItemRepository
				.sumScoreByRoundId(rewardRoundItem.getMiningRewardRound().getId());
		return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRoundItemTrigger.DELIVER, item -> {
			Activity activity = rewardRoundItem.getMiningRewardRound().getActivity();
			Candy candy = new Candy();
			candy.setOwner(item.getAddress());
			candy.setPayer(item.getMiningRewardRound().getActivity().getSupplierAddress());
			candy.setAmount(calcCandyAmount(item, totalScore));
			candy.setAssetCode(activity.getAssetCode());
			candy.setAssetUnit(activity.getAssetUnit());
			candy.setBoxCode(MINING_REWARD_BOX_CODE);
			candy.setActivity(activity);
			candy.setStatus(CandyStatus.CREATED);
			candy = candyRepository.save(candy);
			item.setCandy(candy);
		}));
	}

	private BigDecimal calcCandyAmount(MiningRewardRoundItem item, BigDecimal totalScore) {
		BigDecimal share = item.getScoreSnapshot().divide(totalScore, 20, RoundingMode.DOWN);
		BigDecimal amount = share.multiply(candyAmountPerRound);
		if (amount.stripTrailingZeros().scale() > 6) {
			amount = amount.setScale(4, RoundingMode.DOWN);
		}
		if (amount.compareTo(maxCandyAmount) >= 0) {
			return maxCandyAmount.scaleByPowerOfTen(18);
		}
		if (amount.compareTo(minCandyAmount) <= 0) {
			return minCandyAmount.scaleByPowerOfTen(18);
		}
		return amount.scaleByPowerOfTen(18);
	}
}