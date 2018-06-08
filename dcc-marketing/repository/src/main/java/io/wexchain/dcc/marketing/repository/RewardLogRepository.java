package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.RewardLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;

public interface RewardLogRepository
        extends PagingAndSortingRepository<RewardLog, Long>, JpaSpecificationExecutor<RewardLog> {

    @Query("SELECT SUM(amount) FROM RewardLog WHERE receiverAddress = ?1 AND activityId = ?2")
    BigDecimal findTotalAmount(String address, Long activityId);
}
