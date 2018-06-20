package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.common.constant.MiningRewardRoundItemStatus;
import io.wexchain.dcc.marketing.domain.MiningRewardRoundItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.util.List;

public interface MiningRewardRoundItemRepository
        extends PagingAndSortingRepository<MiningRewardRoundItem, Long>, JpaSpecificationExecutor<MiningRewardRoundItem> {


    List<MiningRewardRoundItem> findByMiningRewardRoundIdAndStatus(Long id, MiningRewardRoundItemStatus status);

    List<MiningRewardRoundItem> findByMiningRewardRoundIdAndStatusOrderByScoreSnapshotDesc(Long id, MiningRewardRoundItemStatus status);

    int countByMiningRewardRoundIdAndStatus(Long id, MiningRewardRoundItemStatus status);

    @Query("SELECT SUM(scoreSnapshot) FROM MiningRewardRoundItem WHERE miningRewardRound.id = ?1")
    BigDecimal sumScoreByRoundId(Long roundId);
}
