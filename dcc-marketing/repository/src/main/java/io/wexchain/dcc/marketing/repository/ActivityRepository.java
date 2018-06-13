package io.wexchain.dcc.marketing.repository;

import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import io.wexchain.dcc.marketing.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface ActivityRepository
        extends PagingAndSortingRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    int countByCreatedTimeBetweenAndStatusIn(Date from, Date to, List<ActivityStatus> statusList);

    Page<Activity> findByCreatedTimeBetweenAndStatusIn(Date from, Date to, List<ActivityStatus> statusList, Pageable pageable);

    Activity findByCode(String code);
}
