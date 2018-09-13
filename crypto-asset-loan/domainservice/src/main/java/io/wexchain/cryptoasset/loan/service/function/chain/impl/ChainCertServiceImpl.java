package io.wexchain.cryptoasset.loan.service.function.chain.impl;

import io.wexchain.cryptoasset.loan.service.function.chain.ChainCertService;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.cert.sdk.service.CertService;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * ChainCertServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ChainCertServiceImpl implements ChainCertService {

    @Resource(name = "idCertService")
    private CertService idCertService;

    @Resource(name = "bankCardCertService")
    private CertService bankCardCertService;

    @Resource(name = "communicationLogCertService")
    private CertService communicationLogCertService;

    @Override
    public CertData getIdCertData(String address) {
        try {
            return idCertService.getData(address);
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public CertData getBankCardCertData(String address) {
        try {
            return bankCardCertService.getData(address);
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }

    @Override
    public CertData getCommunicationLogCertData(String address) {
        try {
            return communicationLogCertService.getData(address);
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }
}
