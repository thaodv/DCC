package io.wexchain.passport.chain.observer.repository.query;

import io.wexchain.passport.chain.observer.common.constant.AddressType;
import io.wexchain.passport.chain.observer.common.constant.TransactionType;
import io.wexchain.passport.chain.observer.common.request.QueryJuzixTransactionRequest;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
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
public class JuzixTransactionQueryBuilder {

    public static Specification<JuzixTransaction> queryTransaction(final QueryJuzixTransactionRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (request.getBlockNumber() != null) {
                predicateList.add(cb.equal(root.get("blockNumber"), request.getBlockNumber()));
            }
            if (StringUtils.isNotBlank(request.getBlockHash())) {
                String lowerCaseBlockHash = request.getBlockHash().toLowerCase();
                if (!lowerCaseBlockHash.startsWith("0x")) {
                    lowerCaseBlockHash = lowerCaseBlockHash + "0x";
                }
                predicateList.add(cb.equal(root.get("blockHash"), lowerCaseBlockHash));
            }
            if (StringUtils.isNotBlank(request.getAddress()) && null != request.getTransactionType()) {
                String lowerCaseAddress = request.getAddress().toLowerCase();
                if (!lowerCaseAddress.startsWith("0x")) {
                    lowerCaseAddress = lowerCaseAddress + "0x";
                }
                if (request.getTransactionType() != null) {
                    if (request.getTransactionType() == TransactionType.TRADE) {
                        Predicate p1 = cb.or(cb.equal(root.get("fromAddress"), lowerCaseAddress), cb.equal(root.get("toAddress"), lowerCaseAddress));
                        Predicate p2 = cb.or(cb.equal(root.get("fromType"), AddressType.CONTRACT), cb.equal(root.get("toType"), AddressType.CONTRACT));
                        predicateList.add(cb.and(p1, p2));
                    }
                    if (request.getTransactionType() == TransactionType.TRANSFER) {
                        Predicate p1 = cb.or(cb.equal(root.get("fromAddress"), lowerCaseAddress), cb.equal(root.get("toAddress"), lowerCaseAddress));
                        Predicate p2 = cb.and(cb.equal(root.get("fromType"), AddressType.EXTERNALLY_OWNED), cb.equal(root.get("toType"), AddressType.EXTERNALLY_OWNED));
                        predicateList.add(cb.and(p1, p2));
                    }
                } else {
                    predicateList.add(cb.or(cb.equal(root.get("fromAddress"), lowerCaseAddress), cb.equal(root.get("toAddress"), lowerCaseAddress)));
                }
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
