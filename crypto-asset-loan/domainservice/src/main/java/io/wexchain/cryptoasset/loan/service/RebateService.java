package io.wexchain.cryptoasset.loan.service;


import io.wexchain.cryptoasset.loan.domain.RebateItem;
import io.wexchain.cryptoasset.loan.domain.RebateOrder;

import java.util.List;
import java.util.Optional;

/**
 * RebateService
 *
 * @author zhengpeng
 */
public interface RebateService {

    RebateOrder rejectOrDeliverFail(Long orderId);

    RebateOrder deliverSuccess(Long orderId);

    Optional<RebateOrder> getRebateOrderByIdNullable(Long orderId);

    RebateOrder getRebateOrder(Long orderId);

    List<RebateItem> getRebateItems(Long orderId);

    List<RebateItem> getRebateItemsAndAmountNotZero(Long orderId);
}
