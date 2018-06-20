package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.api.constant.RewardDeliveryStatus;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import io.wexchain.dcc.marketing.domain.RewardDelivery;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

public interface RewardDeliveryRepository
        extends PagingAndSortingRepository<RewardDelivery, Long>, JpaSpecificationExecutor<RewardDelivery> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("from RewardDelivery where id = ?1")
    RewardDelivery lockById(Long id);

    List<RewardDelivery> findByRewardRoundId(Long rewardRoundId);

    int countByRewardRoundIdAndStatus(Long rewardRoundId, RewardDeliveryStatus status);

    @Query("SELECT SUM(amount) FROM RewardDelivery WHERE rewardRound.id = ?1 AND beneficiaryAddress = ?2")
    BigDecimal sumAmountByAddress(Long rewardRoundId, String address);
}
