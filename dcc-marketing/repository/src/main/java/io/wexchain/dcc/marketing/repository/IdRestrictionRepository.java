package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.IdRestriction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IdRestrictionRepository
        extends PagingAndSortingRepository<IdRestriction, Long>, JpaSpecificationExecutor<IdRestriction> {

    IdRestriction findByScenarioIdAndIdHash(Long id, String idHash);

    IdRestriction findByScenarioCodeAndIdHash(String scenario, String idHash);
}
