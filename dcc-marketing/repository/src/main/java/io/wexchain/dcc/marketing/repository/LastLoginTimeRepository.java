package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.domain.LastLoginTime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface LastLoginTimeRepository
        extends PagingAndSortingRepository<LastLoginTime, Long>, JpaSpecificationExecutor<LastLoginTime> {

    List<LastLoginTime> findByLastLoginTimeAfter(Date date);

    LastLoginTime findByAddress(String address);
}
