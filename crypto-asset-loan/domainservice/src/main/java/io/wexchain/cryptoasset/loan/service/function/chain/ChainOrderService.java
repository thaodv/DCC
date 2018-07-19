package io.wexchain.cryptoasset.loan.service.function.chain;


import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.loan.sdk.contract.Agreement;
import io.wexchain.dcc.loan.sdk.contract.LoanFeeOrder;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.loan.sdk.contract.MortgageLoanOrder;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 链上订单服务类
 * @author zhengpeng
 */
public interface ChainOrderService {

    LoanOrder getLoanOrder(Long chainOrderId);

    void cancel(Long chainOrderId);

    void audit(Long chainOrderId);

    void approve(Long chainOrderId);

    void reject(Long chainOrderId);

    void deliver(Long chainOrderId, String billDigest, String agreementDigest);

    void confirmRepayment(Long chainOrderId);

    void updateRepaymentDigest(Long chainOrderId, String repaymentDigest);

    Pagination<Agreement> queryAgreementPageByIdHashIndex(byte[] idHash, PageParam pageParam);

    Agreement getAgreement(Long id);

    CertData getCertData(String address);

    LoanFeeOrder getFeeOrderById(Long chainOrderId);

    BigDecimal getMinFee();

    BigDecimal getIncentivePercent();

    BigDecimal getRebateTotalFee(Long chainOrderId);

    MortgageLoanOrder getMortgageLoanOrder(Long chainOrderId);
}
