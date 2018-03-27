package io.wexchain.passport.chain.observer.repository;

import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.List;

/**
 * JuzixBlockRepository
 *
 * @author zhengpeng
 */
public interface JuzixTokenTransferRepository
        extends PagingAndSortingRepository<JuzixTokenTransfer, Long>, JpaSpecificationExecutor<JuzixTokenTransfer> {

    @Query(value = "SELECT value FROM t_juzix_token_transfer where transaction_hash = ?1 and contract_address = ?2", nativeQuery = true)
    List<String> findTotalValue(String txHash, String contractAddress);

}
