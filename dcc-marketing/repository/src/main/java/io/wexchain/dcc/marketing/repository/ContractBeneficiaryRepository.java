package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.ContractBeneficiary;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContractBeneficiaryRepository
        extends PagingAndSortingRepository<ContractBeneficiary, Long>, JpaSpecificationExecutor<ContractBeneficiary> {


}
