package io.wexchain.cryptoasset.loan.repository.query;

import io.wexchain.cryptoasset.loan.api.QueryLoanOrderPageRequest;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * LoanOrderQueryBuilder
 *
 * @author zhengpeng
 */
public class LoanOrderQueryBuilder {

    public static Specification<LoanOrder> query(final QueryLoanOrderPageRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (StringUtils.isNotEmpty(request.getMemberId())) {
                predicateList.add(cb.equal(root.get("memberId"), request.getMemberId()));
            }
            if (CollectionUtils.isNotEmpty(request.getExcludeStatusList())) {
                predicateList.add(cb.not(root.get("status").in(request.getExcludeStatusList())));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
