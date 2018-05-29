package io.wexchain.dcc.service.frontend.service.dcc.cert;


/**
 * CertService
 *
 * @author zhengpeng
 */
public interface CertService {
    void validateId(String address);
    void validateBankCard(String address);
    void validateCommunicationLog(String address);

    void validateCert(String address);
}
