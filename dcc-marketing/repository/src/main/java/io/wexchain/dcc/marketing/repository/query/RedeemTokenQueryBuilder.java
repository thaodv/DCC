package io.wexchain.dcc.marketing.repository.query;

import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * RedeemTokenQueryBuilder
 *
 * @author zhengpeng
 */
public class RedeemTokenQueryBuilder {

    public static Specification<RedeemToken> query(QueryRedeemTokenRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(request.getStatusList())) {
                predicateList.add(root.get("status").in(request.getStatusList()));
            }
            if (StringUtils.isNotBlank(request.getAddress())) {
                predicateList.add(cb.equal(root.get("receiverAddress"), request.getAddress()));
            }
            if (StringUtils.isNotBlank(request.getActivityCode())) {
                predicateList.add(cb.equal(root.get("scenario").get("activity").get("code"), request.getActivityCode()));
            }
            if (CollectionUtils.isNotEmpty(request.getScenarioCodeList())) {
                predicateList.add(root.get("scenario").get("code").in(request.getScenarioCodeList()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
