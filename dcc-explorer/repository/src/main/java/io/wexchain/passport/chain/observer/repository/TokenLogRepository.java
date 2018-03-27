package io.wexchain.passport.chain.observer.repository;

import io.wexchain.passport.chain.observer.domain.TokenLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * IcoInvestorRepository
 *
 * @author zhengpeng
 */
public interface TokenLogRepository
        extends PagingAndSortingRepository<TokenLog, Long>, JpaSpecificationExecutor<TokenLog> {


    TokenLog findByTransactionHash(String hash);


}
