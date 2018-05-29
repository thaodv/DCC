/**
 * 
 */
package io.wexchain.dcc.service.frontend.utils;


import java.util.Date;
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

        Date date = new Date();
        System.out.println(date.getTime());
    }
}
