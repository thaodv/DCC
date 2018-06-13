package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.RewardRound;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface RewardRoundRepository
        extends PagingAndSortingRepository<RewardRound, Long>, JpaSpecificationExecutor<RewardRound> {

    RewardRound findByBonusDay(Date day);
}
