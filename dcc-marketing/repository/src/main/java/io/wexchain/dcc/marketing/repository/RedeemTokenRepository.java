package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.RedeemToken;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;

public interface RedeemTokenRepository
        extends PagingAndSortingRepository<RedeemToken, Long>, JpaSpecificationExecutor<RedeemToken> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("from RedeemToken where id = ?1")
    RedeemToken lockById(Long id);

    RedeemToken findByScenarioCodeAndReceiverAddress(String code, String address);

    RedeemToken findByIdAndScenarioIdAndReceiverAddress(Long redeemTokenId, Long id, String address);
}
