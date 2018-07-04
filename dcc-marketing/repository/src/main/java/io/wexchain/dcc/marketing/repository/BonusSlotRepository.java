package io.wexchain.dcc.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import io.wexchain.dcc.marketing.domain.BonusSlot;

public interface BonusSlotRepository
        extends PagingAndSortingRepository<BonusSlot, Long>, JpaSpecificationExecutor<BonusSlot> {

    List<BonusSlot> findByScenarioIdOrderByRankAsc(Long scenarioId);


}
