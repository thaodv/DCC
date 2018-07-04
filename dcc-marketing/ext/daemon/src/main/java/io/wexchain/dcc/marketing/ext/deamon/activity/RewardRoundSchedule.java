package io.wexchain.dcc.marketing.ext.deamon.activity;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wexmarket.topia.commons.basic.competition.LockTemplate2;

import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.RewardRoundService;

/**
 * 自动切换活动状态
 *
 * @author zhengpeng
 */
@Component
public class RewardRoundSchedule {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("cronLockTemplate")
	private LockTemplate2 lockTemplate;

	@Autowired
	private RewardRoundService rewardRoundService;

	@Scheduled(cron = "0 10 0 * * ?")
	public void patrol() {

		lockTemplate.execute("RewardRoundSchedule", () -> {
			logger.info("RewardRound schedule started ");

			DateTime yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay();
			RewardRound rewardRound = rewardRoundService.createRewardRound(yesterday.toDate());

			logger.info("RewardRound schedule ended, id:{}, start block:{}, end block:{}", rewardRound.getId(),
					rewardRound.getStartBlock(), rewardRound.getEndBlock());

			return null;
		});
	}

}
