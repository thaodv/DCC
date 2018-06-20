package io.wexchain.dcc.marketing.ext.deamon.activity;

import com.wexmarket.topia.commons.basic.competition.LockTemplate2;
import io.wexchain.dcc.marketing.domain.MiningRewardRound;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRoundService;
import io.wexchain.dcc.marketing.domainservice.RewardRoundService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动切换活动状态
 *
 * @author zhengpeng
 */
@Component
public class MiningRewardRoundSchedule {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("cronLockTemplate")
	private LockTemplate2 lockTemplate;

	@Autowired
	private MiningRewardRoundService miningRewardRoundService;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void patrol() {

		lockTemplate.execute("MiningRewardRoundSchedule", () -> {
			logger.info("Mining reward round schedule started ");

			DateTime roundTime = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
			MiningRewardRound rewardRound = miningRewardRoundService.createMiningRewardRound(roundTime.toDate());

			return null;
		});
	}

}
