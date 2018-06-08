package io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.dcc.marketing.api.constant.RewardDeliveryStatus;
import io.wexchain.dcc.marketing.api.constant.RewardRoundStatus;
import io.wexchain.dcc.marketing.domain.RewardDelivery;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundTrigger;
import io.wexchain.dcc.marketing.repository.RewardActionRecordRepository;
import io.wexchain.dcc.marketing.repository.RewardDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AnalyzeAdvancer extends AbstractAdvancer<RewardRound, RewardRoundInstruction, RewardRoundTrigger> {

	{
		availableStatus = RewardRoundStatus.SCANNED;
	}

	@Autowired
	private RewardActionRecordRepository rewardActionRecordRepository;

	@Autowired
	private RewardDeliveryRepository rewardDeliveryRepository;

	@Value("${eco.reward.daily.bonus}")
	private BigDecimal ecoRewardDailyBonus;

	@Value("${eco.reward.max.bonus}")
	private BigDecimal ecoRewardMaxBonus;

	@Override
	public AdvancedResult<RewardRound, RewardRoundTrigger> advance(
			RewardRound rewardRound, RewardRoundInstruction instruction, Object message) {


		BigDecimal totalScore = rewardActionRecordRepository.sumScore(rewardRound.getId());

		List<Map<String, String>> addressScoreGroup =
				rewardActionRecordRepository.sumScoreGroupByAddress(rewardRound.getId());

		List<RewardDelivery> deliveryList = new ArrayList<>();
		for (Map<String, String> addressScore : addressScoreGroup) {
			RewardDelivery delivery = new RewardDelivery();
			delivery.setRewardRound(rewardRound);
			delivery.setBeneficiaryAddress(addressScore.get("address"));
			delivery.setAmount(calcEcoAmount(totalScore, new BigDecimal(addressScore.get("totalScore"))));
			delivery.setStatus(RewardDeliveryStatus.CREATED);
			deliveryList.add(delivery);
		}

		rewardDeliveryRepository.saveAll(deliveryList);

		return new AdvancedResult<>(new TriggerBehavior<>(RewardRoundTrigger.ANALYZE));
	}

	private BigDecimal calcEcoAmount(BigDecimal totalScore, BigDecimal addressScore) {
		BigDecimal ecoBonus = addressScore.divide(
				totalScore.multiply(ecoRewardDailyBonus), 4, RoundingMode.DOWN);
		return ecoBonus.min(ecoRewardMaxBonus);
	}

}