package io.wexchain.dcc.marketing.repository.query;

import io.wexchain.dcc.marketing.domain.RewardLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * RewardLogQueryBuilder
 *
 * @author zhengpeng
 */
public class RewardLogQueryBuilder {

    public static Specification<RewardLog> query(String address, Long activityId, Long scenarioId) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(address)) {
                predicateList.add(cb.equal(root.get("receiverAddress"), address));
            }
            if (activityId != null) {
                predicateList.add(cb.equal(root.get("activityId"), activityId));
            }
            if (scenarioId != null) {
                predicateList.add(cb.equal(root.get("scenarioId"), scenarioId));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
