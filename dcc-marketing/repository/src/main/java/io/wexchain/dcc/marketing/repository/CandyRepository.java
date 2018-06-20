package io.wexchain.dcc.marketing.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import io.wexchain.dcc.marketing.api.constant.CandyStatus;
import io.wexchain.dcc.marketing.domain.Candy;

public interface CandyRepository extends PagingAndSortingRepository<Candy, Long>, JpaSpecificationExecutor<Candy> {

	List<Candy> findByOwnerAndBoxCodeOrderByIdAsc(String owner, String boxCode);

	int countByOwnerAndBoxCode(String owner, String boxCode);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from Candy where id = ?1")
	Optional<Candy> lockById(Long id);

	Integer countByOwnerAndBoxCodeAndStatus(String address, String boxCode, CandyStatus status);

	List<Candy> findTop1000ByStatusOrderByIdAsc(CandyStatus status);
}
