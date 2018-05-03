package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.Scenario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ScenarioRepository
        extends PagingAndSortingRepository<Scenario, Long>, JpaSpecificationExecutor<Scenario> {

    Scenario findByCode(String scenarioCode);

    List<Scenario> findByActivityCodeOrderById(String activityCode);

}
