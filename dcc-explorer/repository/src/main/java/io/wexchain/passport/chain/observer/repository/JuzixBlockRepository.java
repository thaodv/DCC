package io.wexchain.passport.chain.observer.repository;

import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * JuzixBlockRepository
 *
 * @author zhengpeng
 */
public interface JuzixBlockRepository
        extends PagingAndSortingRepository<JuzixBlock, Long>, JpaSpecificationExecutor<JuzixBlock> {

    @Query("SELECT MAX(blockNumber) FROM JuzixBlock")
    Long findMaxNumber();

    JuzixBlock findByBlockNumber(Long number);

    JuzixBlock findByHash(String hash);
}
