package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.RedeemToken;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RedeemTokenRepository
        extends PagingAndSortingRepository<RedeemToken, Long>, JpaSpecificationExecutor<RedeemToken> {

    RedeemToken findByScenarioCodeAndReceiverAddress(String code, String address);
}
