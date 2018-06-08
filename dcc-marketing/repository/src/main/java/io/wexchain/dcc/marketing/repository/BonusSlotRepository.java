package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.BonusSlot;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BonusSlotRepository
        extends PagingAndSortingRepository<BonusSlot, Long>, JpaSpecificationExecutor<BonusSlot> {

    List<BonusSlot> findByScenarioIdOrderByRankAsc(Long scenarioId);


}
