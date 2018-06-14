package io.wexchain.dcc.marketing.domainservice.function.chain.impl;

import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.cert.sdk.service.CertService;
import io.wexchain.dcc.marketing.domainservice.function.chain.ChainOrderService;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
