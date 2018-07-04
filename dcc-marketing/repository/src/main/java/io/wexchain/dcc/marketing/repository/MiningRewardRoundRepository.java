package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.MiningRewardRound;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface MiningRewardRoundRepository
        extends PagingAndSortingRepository<MiningRewardRound, Long>, JpaSpecificationExecutor<MiningRewardRound> {

    MiningRewardRound findByRoundTime(Date roundTime);

}
