/**
 * 
 */
package io.wexchain.dcc.service.frontend.utils;

import io.wexchain.dcc.service.frontend.common.constants.ApiJsonStringConstants;

import java.util.UUID;


/**
 * <p>
 * 请求订单号工具类
 * </p>
 * 
 * @author yanyi
 */
public class ReqNoUtil {

    /**
     * 获得请求订单号
     */
    public static String getReqNo() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(ApiJsonStringConstants.API_LIST.size());
    }
}
