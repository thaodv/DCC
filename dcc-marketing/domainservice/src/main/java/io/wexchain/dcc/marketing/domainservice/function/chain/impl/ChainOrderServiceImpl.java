package io.wexchain.dcc.marketing.domainservice.function.chain.impl;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.cert.sdk.service.CertService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;

/**
 * ChainOrderServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ChainOrderServiceImpl implements ChainOrderService {

    @Autowired
    private CertService certService;

    @Override
    public Optional<String> getIdHash(String address) {
        try {
            CertData certData = certService.getData(address);
            if (certData != null && certData.getContent().getDigest1().length > 0) {
                return Optional.of(Base64Utils.encodeToString(certData.getContent().getDigest1()));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
    }
}
