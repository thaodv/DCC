package io.wexchain.cryptoasset.loan.service.function.chain.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.cert.sdk.service.CertService;
import io.wexchain.dcc.loan.sdk.contract.*;
import io.wexchain.dcc.loan.sdk.service.AgreementService;
import io.wexchain.dcc.loan.sdk.service.LoanFeeService;
import io.wexchain.dcc.loan.sdk.service.LoanService;
import io.wexchain.dcc.loan.sdk.service.MortgageLoanService;
import io.wexchain.dcc.sdk.client.nonce.NonceCreator;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ChainOrderServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ChainOrderServiceImpl implements ChainOrderService {

    public static final BigDecimal PERCENT = BigDecimal.valueOf(100);

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanFeeService loanFeeService;

    @Autowired
    private AgreementService agreementService;

    @Resource(name = "idCertService")
    private CertService certService;

    @Autowired
    private MortgageLoanService mortgageLoanService;

    @Autowired
    @Qualifier("loanSdkCredentials")
    private Credentials loanSdkCredentials;

    @Override
    public LoanOrder getLoanOrder(Long chainOrderId) {
        try {
            return ErrorCodeValidate.notNull(loanService.getOrder(chainOrderId), CalErrorCode.CHAIN_ORDER_NOT_FOUND);
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public MortgageLoanOrder getMortgageLoanOrder(Long chainOrderId) {
            return mortgageLoanService.getOrder(chainOrderId);
    }

    @Override
    public LoanFeeOrder getFeeOrderById(Long chainOrderId) {
        return loanFeeService.getFeeOrderById(chainOrderId);
    }

    @Override
    public BigDecimal getMinFee() {
        return AmountScaleUtil.cah2Cal(loanFeeService.getMinFee());
    }

    @Override
    public BigDecimal getIncentivePercent() {
        BigDecimal incentivePercent = BigDecimal.valueOf(loanFeeService.getIncentivePercent().intValue());
        BigDecimal percentValue = incentivePercent.divide(PERCENT);
        return percentValue;
    }

    @Override
    public BigDecimal getRebateTotalFee(Long chainOrderId) {
        LoanOrder loanOrder = getLoanOrder(chainOrderId);
        BigDecimal inputFee = AmountScaleUtil.cah2Cal(loanOrder.getFee());
        BigDecimal percentValue = getIncentivePercent();
        return inputFee.subtract(inputFee.multiply(percentValue));
    }

    @Override
    public Pagination<Agreement> queryAgreementPageByIdHashIndex(byte[] idHash, PageParam pageParam) {
        String idHashBase64 = Base64Utils.encodeToUrlSafeString(idHash);
        return agreementService.queryAgreementPageByIdHashIndex(idHashBase64, pageParam);
    }

    @Override
    public Agreement getAgreement(Long id) {
        return agreementService.getAgreement(id);
    }

    @Override
    public void cancel(Long chainOrderId) {
        try {
            LoanOrder chainOrder = getLoanOrder(chainOrderId);
            if (chainOrder.getStatus() == OrderStatus.CREATED) {
                loanService.cancel(loanSdkCredentials, NonceCreator::create, chainOrderId)
                        .getEvents().orElseThrow(() ->
                        new ErrorCodeException(CalErrorCode.CHAIN_ORDER_UPDATE_FAIL.name(), "取消失败"));
            }
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public void audit(Long chainOrderId) {
        try {
            LoanOrder chainOrder = getLoanOrder(chainOrderId);
            if (chainOrder.getStatus() == OrderStatus.CREATED) {
                loanService.audit(loanSdkCredentials, NonceCreator::create, chainOrderId)
                        .getEvents().orElseThrow(() ->
                        new ErrorCodeException(CalErrorCode.CHAIN_ORDER_UPDATE_FAIL.name(), "审核失败"));
            }
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public void approve(Long chainOrderId) {
        try {
            LoanOrder chainOrder = getLoanOrder(chainOrderId);
            if (chainOrder.getStatus() == OrderStatus.AUDITING) {
                loanService.approve(loanSdkCredentials, NonceCreator::create, chainOrderId)
                        .getEvents().orElseThrow(() ->
                        new ErrorCodeException(CalErrorCode.CHAIN_ORDER_UPDATE_FAIL.name(), "审核通过失败"));
            }
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public void reject(Long chainOrderId) {
        try {
            LoanOrder chainOrder = getLoanOrder(chainOrderId);
            if (chainOrder.getStatus() == OrderStatus.AUDITING) {
                loanService.reject(loanSdkCredentials, NonceCreator::create, chainOrderId)
                        .getEvents().orElseThrow(() ->
                        new ErrorCodeException(CalErrorCode.CHAIN_ORDER_UPDATE_FAIL.name(), "审核拒绝失败"));
            }
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public void deliver(Long chainOrderId, String billDigest, String agreementDigest) {
        try {
            LoanOrder chainOrder = getLoanOrder(chainOrderId);
            if (chainOrder.getStatus() == OrderStatus.APPROVED) {
                loanService.deliver(loanSdkCredentials, NonceCreator::create, chainOrderId,
                        billDigest.getBytes("UTF-8"), agreementDigest.getBytes("UTF-8"))
                        .getEvents().orElseThrow(() ->
                        new ErrorCodeException(CalErrorCode.CHAIN_ORDER_UPDATE_FAIL.name(), "放款失败"));
            }
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public void confirmRepayment(Long chainOrderId) {
        try {
            LoanOrder chainOrder = getLoanOrder(chainOrderId);
            if (chainOrder.getStatus() == OrderStatus.DELIVERIED) {
                loanService.confirmRepay(loanSdkCredentials, NonceCreator::create, chainOrderId)
                        .getEvents().orElseThrow(() ->
                        new ErrorCodeException(CalErrorCode.CHAIN_ORDER_UPDATE_FAIL.name(), "确认还款失败"));
            }
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public void updateRepaymentDigest(Long chainOrderId, String repaymentDigest) {
        try {
            loanService.updateRepayDigest(
                    loanSdkCredentials, NonceCreator::create, chainOrderId,
                    repaymentDigest.getBytes("UTF-8"));
        } catch (IOException | CipherException | InterruptedException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public CertData getCertData(String address) {
        try {
            CertData certData = certService.getData(address);
            ErrorCodeValidate.isTrue(
                    certData != null && certData.getContent().getDigest1().length > 0
                    , CalErrorCode.CERT_INFO_NOT_FOUND);
            return certData;
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }


}
