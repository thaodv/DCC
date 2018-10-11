package io.wexchain.dcc.marketing.domainservice.function.chain;


import java.util.Optional;

/**
 * 链上订单服务类
 * @author zhengpeng
 */
public interface ChainOrderService {

    Optional<String> getIdHash(String address);

}
