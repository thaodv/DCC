package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.RewardActionRecord;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RewardActionRecordRepository
        extends PagingAndSortingRepository<RewardActionRecord, Long>, JpaSpecificationExecutor<RewardActionRecord> {

    int countByEcoRewardRuleIdAndIdHash(Long ruleId, String idHash);

    @Query("SELECT AR.address, SUM(AR.score) AS totalScore " +
            "FROM RewardActionRecord AR " +
            "WHERE AR.rewardRound.id = ?1 AND AR.status = 'ACCEPTED' GROUP BY AR.address")
    List<Map<String, String>> sumScoreGroupByAddress(Long rewardRoundId);

    @Query("SELECT SUM(AR.score) " +
            "FROM RewardActionRecord AR " +
            "WHERE AR.rewardRound.id = ?1 AND AR.status = 'ACCEPTED'")
    BigDecimal sumScore(Long rewardRoundId);
}
