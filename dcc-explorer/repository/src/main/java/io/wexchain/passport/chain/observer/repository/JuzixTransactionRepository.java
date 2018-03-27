package io.wexchain.passport.chain.observer.repository;

import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * JuzixBlockRepository
 *
 * @author zhengpeng
 */
public interface JuzixTransactionRepository
        extends PagingAndSortingRepository<JuzixTransaction, Long>, JpaSpecificationExecutor<JuzixTransaction> {

    JuzixTransaction findByHash(String hash);

    int countByBlockTimestampBetween(Date start, Date end);
}
