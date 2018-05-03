package io.wexchain.dcc.service.frontend.service.marketing;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface IdHashService {

    String getIdHashByAddress(String address);

    Boolean getVerifyStatus(String bizCode, String address);

}
