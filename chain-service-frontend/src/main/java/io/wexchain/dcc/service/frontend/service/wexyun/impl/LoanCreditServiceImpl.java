package io.wexchain.dcc.service.frontend.service.wexyun.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexyun.open.api.request.credit.credit2.Credit2ApplyAddRequest;
import io.wexchain.dcc.loan.sdk.contract.LoanOrder;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import io.wexchain.dcc.loan.sdk.service.LoanService;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.wexyun.Credit2OperationClient;
import io.wexchain.dcc.service.frontend.integration.wexyun.MemberOperationClient;
import io.wexchain.dcc.service.frontend.model.request.LoanCreditApplyRequest;
import io.wexchain.dcc.service.frontend.service.wexyun.ExternalIdCreator;
import io.wexchain.dcc.service.frontend.service.wexyun.LoanCreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.soap.Detail;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * LoanCreditServiceImpl
 *
 */
@Service(value = "loanCreditService")
public class LoanCreditServiceImpl implements LoanCreditService{

    private static final Logger logger = LoggerFactory.getLogger(LoanCreditServiceImpl.class);

    @Value(value = "${app.identity}")
    private String appIdentity;

    @Autowired
    private MemberOperationClient memberOperationClient;
    @Autowired
    private ExternalIdCreator externalIdCreator;
    @Autowired
    private LoanService loanService;
    @Autowired
    private Credit2OperationClient credit2OperationClient;

    @Override
    public Long apply(LoanCreditApplyRequest credit2ApplyAddRequest, Long memberId) {
        try {
            LoanOrder loanOrder = ErrorCodeValidate.notNull(
                    loanService.getOrder(credit2ApplyAddRequest.getOrderId()),
                    FrontendErrorCode.ORDER_NOT_FOUND);
            ErrorCodeValidate.isTrue(loanOrder.getStatus() == OrderStatus.CREATED,
                    FrontendErrorCode.INVALID_ORDER_STATUS);

            Credit2ApplyAddRequest applyAddRequest = new Credit2ApplyAddRequest();
            applyAddRequest.setBorrowerId(memberId.toString());
            applyAddRequest.setBorrowName(credit2ApplyAddRequest.getBorrowName());
            applyAddRequest.setBorrowAmount(credit2ApplyAddRequest.getBorrowAmount());
            applyAddRequest.setBorrowDuration(credit2ApplyAddRequest.getBorrowDuration());
            applyAddRequest.setDurationType(credit2ApplyAddRequest.getDurationType());
            applyAddRequest.setCertNo(credit2ApplyAddRequest.getCertNo());
            applyAddRequest.setMobile(credit2ApplyAddRequest.getMobile());
            applyAddRequest.setBankCard(credit2ApplyAddRequest.getBankCard());
            applyAddRequest.setBankMobile(credit2ApplyAddRequest.getBankMobile());
            applyAddRequest.setSourceIdentity(appIdentity);
            applyAddRequest.setExternalApplyId(externalIdCreator.getExternalId(credit2ApplyAddRequest.getOrderId().toString()));
            applyAddRequest.setReceiveMemberId(memberId.toString());

            Map<String, String> itemDetailMap = new HashMap<>();
            itemDetailMap.put("loanProductId",credit2ApplyAddRequest.getLoanProductId().toString());
            applyAddRequest.setItemDetailMap(itemDetailMap);
            Long apply = credit2OperationClient.apply(applyAddRequest);
            return apply;
        } catch (IOException e) {
            logger.error("验证银行卡摘要系统错误：", e);
            throw new RuntimeException(e);
        }

    }
}
