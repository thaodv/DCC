package io.wexchain.cryptoasset.loan.repository;

import io.wexchain.cryptoasset.loan.api.constant.CollectOrderStatus;
import io.wexchain.cryptoasset.loan.domain.CollectOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;

public interface CollectOrderRepository
		extends PagingAndSortingRepository<CollectOrder, Long>, JpaSpecificationExecutor<CollectOrder> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from CollectOrder where id = ?1")
	CollectOrder lockById(Long id);

    int countByCreatedTimeBetweenAndStatusIn(Date beginTime, Date endTime, List<CollectOrderStatus> collectOrderStatuses);

	Page<CollectOrder> findByCreatedTimeBetweenAndStatusIn(
			Date beginTime, Date endTime, List<CollectOrderStatus> collectOrderStatuses, Pageable pageable);
}
