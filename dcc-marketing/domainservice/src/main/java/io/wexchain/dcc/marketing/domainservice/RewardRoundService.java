package io.wexchain.dcc.marketing.domainservice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;

import io.wexchain.dcc.marketing.api.model.EcoRewardRankVo;
import io.wexchain.dcc.marketing.api.model.EcoRewardStatisticsInfo;
import io.wexchain.dcc.marketing.domain.RewardRound;

public interface RewardRoundService {

    RewardRound createRewardRound(Date bonusDay);

    Optional<RewardRound> findRewardRoundByBonusDay(Date bonusDay);

    EcoRewardStatisticsInfo getEcoRewardStatisticsInfo(String address);

    BigDecimal getEcoScore();

    List<EcoRewardRankVo> queryEcoRewardRankList(DateTime roundTime);

}
