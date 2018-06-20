package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;

public interface MiningRewardRecordRepository
        extends PagingAndSortingRepository<MiningRewardRecord, Long>, JpaSpecificationExecutor<MiningRewardRecord> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("from MiningRewardRecord where id = ?1")
    MiningRewardRecord lockById(Long id);

    Page<MiningRewardRecord> findByAddress(String address, Pageable pageable);
}
