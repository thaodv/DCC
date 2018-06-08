package io.wexchain.dcc.marketing.repository.query;

import io.wexchain.dcc.marketing.domain.BonusSlot;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * BonusSlotQueryBuilder
 *
 * @author zhengpeng
 */
public class BonusSlotQueryBuilder {

    public static Specification<BonusSlot> query(Long scenarioId) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.equal(root.get("scenario").get("id"), scenarioId));
            predicateList.add(cb.greaterThan(root.get("currentCount"), 0));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
