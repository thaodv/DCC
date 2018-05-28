package io.wexchain.cryptoasset.loan.service.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.domain.CollectOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.repository.CollectOrderRepository;
import io.wexchain.cryptoasset.loan.service.CollectOrderService;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import io.wexchain.cryptoasset.loan.service.processor.order.collect.CollectOrderInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * CollectOrderServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class CollectOrderServiceImpl implements CollectOrderService {

    @Autowired
    private CollectOrderRepository collectOrderRepository;

    @Autowired
    private CryptoAssetLoanService cryptoAssetLoanService;

    @Resource(name = "collectOrderExecutor")
    private OrderExecutor<CollectOrder, CollectOrderInstruction> collectOrderExecutor;

    @Override
    public CollectOrder collect(LoanOrder loanOrder) {
        CollectOrder collectOrder = collectOrderRepository
                .findById(loanOrder.getId())
                .orElseThrow(() -> new ErrorCodeException(CalErrorCode.ORDER_NOT_FOUND.name(), "归集订单未找到"));
        collectOrderExecutor.executeAsync(collectOrder, null, null);
        return collectOrder;
    }
}
