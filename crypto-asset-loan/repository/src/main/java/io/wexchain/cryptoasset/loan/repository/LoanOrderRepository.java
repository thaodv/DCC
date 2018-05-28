package io.wexchain.cryptoasset.loan.repository;


import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
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

public interface LoanOrderRepository
		extends PagingAndSortingRepository<LoanOrder, Long>, JpaSpecificationExecutor<LoanOrder> {


	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from LoanOrder where id = ?1")
	LoanOrder lockById(Long id);

	/**
	 * 查询借款订单
	 * @param chainOrderId 链上订单ID
	 * @return 借款订单
	 */
	LoanOrder findByChainOrderId(Long chainOrderId);

	/**
	 * 查询借款订单
	 * @param applyId 进件ID
	 * @return 借款订单
	 */
    LoanOrder findByApplyId(String applyId);

	LoanOrder findByChainOrderIdAndMemberId(Long chainOrderId, String memberId);


    int countByCreatedTimeBetweenAndStatusIn(
    		Date beginTime, Date endTime, List<LoanOrderStatus> loanOrderStatuses);

	Page<LoanOrder> findByCreatedTimeBetweenAndStatusIn(
			Date beginTime, Date endTime, List<LoanOrderStatus> loanOrderStatuses, Pageable pageable);
}
