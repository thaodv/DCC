package io.wexchain.dcc.service.frontend.service.dcc.cert;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.service.frontend.common.constants.FrontendWebConstants;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * LoanCreditServiceImpl
 *
 */
public class CertServiceImpl implements CertService,FrontendWebConstants{

    private static final Logger logger = LoggerFactory.getLogger(CertServiceImpl.class);

    private Map<String, io.wexchain.dcc.cert.sdk.service.CertService> certServiceMap;

    @Override
    public void validateId(String address) {
        ErrorCodeValidate.isTrue(validate(ID,address),
                FrontendErrorCode.CERT_ID_EXCEED);
    }
    @Override
    public void validateBankCard(String address) {
        ErrorCodeValidate.isTrue(validate(BANK_CARD,address),
                FrontendErrorCode.BANK_CARD_EXCEED);
    }
    @Override
    public void validateCommunicationLog(String address) {
        ErrorCodeValidate.isTrue(validate(COMMUNICATION_LOG,address),
                FrontendErrorCode.COMMUNICATION_LOG_EXCEED);
    }

    @Override
    public void validateCert(String address) {
        validateId(address);
        validateBankCard(address);
        validateCommunicationLog(address);
    }

    private boolean validate(String business,String address){
        try {
            CertData data = certServiceMap.get(business).getData(address);

            long nowTime = new Date().getTime();

            if(data != null && data.getContent().getExpired() != 0){
                String expiredStr = String.valueOf(data.getContent().getExpired());
                int num =  13 - expiredStr.length();
                if(num >0 ){
                    for (int i = 0 ; i < num ; i++){
                        expiredStr = expiredStr.concat("0");
                    }
                }

                if(Long.parseLong(expiredStr) > nowTime){
                    return true;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取链上认证数据异常{}",e);
        }
        return false;
    }

    public void setCertServiceMap(Map<String, io.wexchain.dcc.cert.sdk.service.CertService> certServiceMap) {
        this.certServiceMap = certServiceMap;
    }
}
