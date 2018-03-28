package io.wexchain.passport.chain.observer.repository.query;

import io.wexchain.passport.chain.observer.common.request.QueryJuzixTokenTransferRequest;
import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * JuzixTransactionQueryBuilder
 *
 * @author zhengpeng
 */
public class JuzixTokenTransferQueryBuilder {

    public static Specification<JuzixTokenTransfer> query(final QueryJuzixTokenTransferRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getTransactionHash())) {
                predicateList.add(cb.equal(root.get("transactionHash"), request.getTransactionHash().toLowerCase()));
            }
            if (StringUtils.isNotBlank(request.getAddress())) {
                String lowerCaseAddress = request.getAddress().toLowerCase();
                predicateList.add(cb.or(cb.equal(root.get("fromAddress"), lowerCaseAddress), cb.equal(root.get("toAddress"), lowerCaseAddress)));
            }
            if (StringUtils.isNotEmpty(request.getContractAddress())) {
                predicateList.add(cb.equal(root.get("contractAddress"), request.getContractAddress().toLowerCase()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
