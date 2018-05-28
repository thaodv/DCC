package io.wexchain.cryptoasset.loan.service.function.wexyun.impl;

import com.weihui.finance.common.credit2.domain.result.UploadChainNotifyResult;
import com.weihui.mq.handler.notify.AbstractNotifyMessageHandler;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * WexyunMessageHandler
 */
public class WexyunMessageHandler extends AbstractNotifyMessageHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CryptoAssetLoanService cryptoAssetLoanService;

    @Override
    public void handleMessage(Object request) throws Exception {

        System.out.println("Receive the wexyun apply message, object is" + request.getClass().getName());

        UploadChainNotifyResult uploadChainNotifyResult = (UploadChainNotifyResult) request;
        logger.info("Receive the wexyun apply message, applyId:{}, processStatus:{}",
                uploadChainNotifyResult.getApplyId(), uploadChainNotifyResult.getProcessStatus());

        cryptoAssetLoanService.advanceByApplyIdAsync(String.valueOf(uploadChainNotifyResult.getApplyId()));

        logger.info("Advance loan order success");
    }



}
