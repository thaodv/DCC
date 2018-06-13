package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.api.constant.ParticipatorRole;
import io.wexchain.dcc.marketing.domain.EcoRewardRule;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EcoRewardRuleRepository
        extends PagingAndSortingRepository<EcoRewardRule, Long>, JpaSpecificationExecutor<EcoRewardRule> {

    List<EcoRewardRule> findByParticipatorRoleOrderByIdAsc(ParticipatorRole participatorRole);
}
