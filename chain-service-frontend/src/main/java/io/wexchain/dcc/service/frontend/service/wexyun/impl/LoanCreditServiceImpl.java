package io.wexchain.dcc.service.frontend.service.wexyun.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import io.wexchain.dcc.loan.sdk.service.LoanService;
import io.wexchain.dcc.service.frontend.common.constants.FrontendWebConstants;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.integration.wexyun.Credit2OperationClient;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.model.vo.LoanProductVo;
import io.wexchain.dcc.service.frontend.service.dcc.cert.CertService;
import io.wexchain.dcc.service.frontend.service.dcc.loanProduct.LoanProductService;
import io.wexchain.dcc.service.frontend.service.wexyun.ExternalIdCreator;
import io.wexchain.dcc.service.frontend.service.wexyun.LoanCreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LoanCreditServiceImpl
 *
 */
@Service(value = "loanCreditService")
public class LoanCreditServiceImpl implements LoanCreditService,FrontendWebConstants{

    private static final Logger logger = LoggerFactory.getLogger(LoanCreditServiceImpl.class);

    @Value(value = "${app.identity}")
    private String appIdentity;
    @Autowired
    private ExternalIdCreator externalIdCreator;
    @Autowired
    private LoanService loanService;
    @Autowired
    private Credit2OperationClient credit2OperationClient;
    @Autowired
    private CertService certService;
    @Autowired
    private LoanProductService loanProductService;

    @Override
    public Long apply(LoanCreditApplyRequest credit2ApplyAddRequest, MemberDetails memberDetails) {
        Long memberId = memberDetails.getId();
        certService.validateCert(memberDetails.getUsername());
        try {
            LoanOrder loanOrder = ErrorCodeValidate.notNull(
                    loanService.getOrder(credit2ApplyAddRequest.getOrderId()),
                    FrontendErrorCode.ORDER_NOT_FOUND);
            ErrorCodeValidate.isTrue(loanOrder.getStatus() == OrderStatus.CREATED,
                    FrontendErrorCode.INVALID_ORDER_STATUS);
            LoanProductVo loanProductVo = loanProductService.getLoanProductVo(credit2ApplyAddRequest.getLoanProductId());
            Credit2ApplyAddRequest applyAddRequest = new Credit2ApplyAddRequest();
            applyAddRequest.setBorrowerId(memberId.toString());
            applyAddRequest.setBorrowName(credit2ApplyAddRequest.getBorrowName());
            applyAddRequest.setBorrowAmount(credit2ApplyAddRequest.getBorrowAmount().multiply(AMOUNT_MULTIPLY).setScale(2));
            applyAddRequest.setBorrowDuration(credit2ApplyAddRequest.getBorrowDuration());
            applyAddRequest.setDurationType(credit2ApplyAddRequest.getDurationType());
            applyAddRequest.setCertNo(credit2ApplyAddRequest.getCertNo());
            applyAddRequest.setMobile(credit2ApplyAddRequest.getMobile());
            applyAddRequest.setBankCard(credit2ApplyAddRequest.getBankCard());
            applyAddRequest.setBankMobile(credit2ApplyAddRequest.getBankMobile());
            applyAddRequest.setSourceIdentity(appIdentity);
            applyAddRequest.setExternalApplyId(externalIdCreator.getExternalId(memberId.toString(),credit2ApplyAddRequest.getOrderId().toString()));
            applyAddRequest.setReceiveMemberId(memberId.toString());
            applyAddRequest.setLoanType(loanProductVo.getLoanType());
            applyAddRequest.setApplyDate(new Date(credit2ApplyAddRequest.getApplyDate()));
            Map<String, String> itemDetailMap = new HashMap<>();
            itemDetailMap.put("loanProductId",credit2ApplyAddRequest.getLoanProductId().toString());
            itemDetailMap.put("agreementExptectAnnuRate",loanProductVo.getLoanRate().toString());
            itemDetailMap.put("agreementRepayMode","TOGETHER");
            applyAddRequest.setItemDetailMap(itemDetailMap);
            Long apply = credit2OperationClient.apply(applyAddRequest);
            return apply;
        } catch (IOException e) {
            logger.error("验证银行卡摘要系统错误：", e);
            throw new RuntimeException(e);
        }

    }
}
