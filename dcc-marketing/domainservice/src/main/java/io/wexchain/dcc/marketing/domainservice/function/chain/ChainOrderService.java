package io.wexchain.dcc.marketing.domainservice.function.chain;


import io.wexchain.dcc.cert.sdk.contract.CertData;

import java.util.Optional;

/**
 * 链上订单服务类
 * @author zhengpeng
 */
public interface ChainOrderService {

    Optional<String> getIdHash(String address);
}
