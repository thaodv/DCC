package io.wexchain.dcc.marketing.repository.query;

import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * ActivityQueryBuilder
 *
 * @author zhengpeng
 */
public class ActivityQueryBuilder {

    public static Specification<Activity> query(QueryActivityRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(request.getStatusList())) {
                predicateList.add(root.get("status").in(request.getStatusList()));
            }
            if (StringUtils.isNotBlank(request.getMerchantCode())) {
                predicateList.add(cb.equal(root.get("merchantCode"), request.getMerchantCode()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
