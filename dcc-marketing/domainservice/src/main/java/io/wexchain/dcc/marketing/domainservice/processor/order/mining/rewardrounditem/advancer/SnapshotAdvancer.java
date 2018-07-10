package io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.cryptoasset.account.api.model.Account;
import io.wexchain.dcc.marketing.common.constant.MiningRewardRoundItemStatus;
import io.wexchain.dcc.marketing.domain.MiningRewardRoundItem;
import io.wexchain.dcc.marketing.domainservice.function.booking.BookingService;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.MiningRewardRoundItemInstruction;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardrounditem.MiningRewardRoundItemTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class SnapshotAdvancer extends AbstractAdvancer<MiningRewardRoundItem,
		MiningRewardRoundItemInstruction, MiningRewardRoundItemTrigger> {

	{
		availableStatus = MiningRewardRoundItemStatus.CREATED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BookingService bookingService;

	@Override
	public AdvancedResult<MiningRewardRoundItem, MiningRewardRoundItemTrigger> advance(
			MiningRewardRoundItem rewardRoundItem, MiningRewardRoundItemInstruction instruction, Object message) {

		Account account = bookingService.getAccountByCode(rewardRoundItem.getAddress());
		if (account == null) {
			logger.warn("Mining reward item address {} is null", rewardRoundItem.getAddress());
			return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRoundItemTrigger.SNAPSHOT, item -> {
				item.setScoreSnapshot(BigDecimal.ZERO);
			}), NextOperation.PAUSE);
		}
		return new AdvancedResult<>(new TriggerBehavior<>(MiningRewardRoundItemTrigger.SNAPSHOT, item -> {
			item.setScoreSnapshot(account.getBalance());
		}), NextOperation.PAUSE);
	}
}