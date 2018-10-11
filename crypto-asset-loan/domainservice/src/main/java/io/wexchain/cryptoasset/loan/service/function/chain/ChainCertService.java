package io.wexchain.cryptoasset.loan.service.function.chain;


import io.wexchain.dcc.cert.sdk.contract.CertData;

/**
 *
 * @author zhengpeng
 */
public interface ChainCertService {

    CertData getIdCertData(String address);

    CertData getBankCardCertData(String address);

    CertData getCommunicationLogCertData(String address);
}
