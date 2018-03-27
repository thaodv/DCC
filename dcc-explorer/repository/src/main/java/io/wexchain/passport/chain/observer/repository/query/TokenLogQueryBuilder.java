package io.wexchain.passport.chain.observer.repository.query;

import io.wexchain.passport.chain.observer.domain.TokenLog;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * KycInfoQueryBuilder
 *
 * @author zhengpeng
 */
public class TokenLogQueryBuilder {

    public static Specification<TokenLog> build(String contractAddress, String walletAddress) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Predicate c1 = cb.or(cb.equal(root.get("contractAddress"), contractAddress.toLowerCase()), cb.equal(root.get("fromAddress"), walletAddress.toLowerCase()));
            Predicate c2 = cb.and(cb.equal(root.get("contractAddress"), contractAddress.toLowerCase()), cb.equal(root.get("toAddress"), walletAddress.toLowerCase()));
            predicateList.add(cb.or(c1, c2));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
