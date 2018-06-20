package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.domain.CoolDownConfig;
import io.wexchain.dcc.marketing.domain.MiningRewardRound;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Optional;

/**
 * MiningRewardRoundService
 *
 * @author zhengpeng
 */
public interface MiningRewardRoundService {

    Optional<MiningRewardRound> getMiningRewardRoundNullable(Date roundTime);

    MiningRewardRound createMiningRewardRound(Date roundTime);

}
