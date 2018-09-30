package io.wexchain.dcc.marketing.domainservice;

import java.util.Date;
import java.util.Optional;

import io.wexchain.dcc.marketing.domain.MiningRewardRound;

/**
 * MiningRewardRoundService
 *
 * @author zhengpeng
 */
public interface MiningRewardRoundService {

    Optional<MiningRewardRound> getMiningRewardRoundNullable(Date roundTime);

    MiningRewardRound createMiningRewardRound(Date roundTime);

}
