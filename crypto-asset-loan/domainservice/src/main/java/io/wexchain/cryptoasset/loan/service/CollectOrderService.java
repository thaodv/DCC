package io.wexchain.cryptoasset.loan.service;

import io.wexchain.cryptoasset.loan.domain.CollectOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;

import java.math.BigDecimal;

/**
 * CollectOrderService
 *
 * @author zhengpeng
 */
public interface CollectOrderService {

    CollectOrder collect(LoanOrder loanOrder);


}
