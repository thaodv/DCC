/**
 * 
 */
package io.wexchain.dcc.service.frontend.utils;

import io.wexchain.dcc.service.frontend.common.constants.FrontendWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * IP地址工具类
 * </p>
 * 
 * @author yanyi
 */
public class AccessUtil {

    // 获取客户端请求ip的头参数
    private static final String[] HTTP_HEADER_IP_ADDRESS_ALL = { "x-forwarded-for", "Cdn-Src-Ip", "Proxy-Client-IP",
                                                                 "WL-Proxy-Client-IP" };
    private static Logger LOGGER                     = LoggerFactory.getLogger(AccessUtil.class);

    /**
     * 获取客户端请求的IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        List<String> ips = new ArrayList<String>();
        for (String header : HTTP_HEADER_IP_ADDRESS_ALL) {
            String ip = request.getHeader(header);
            if (isIpLegal(ip)) {
                ips.add(ip);
            }
        }
        if (ips.isEmpty()) {
            String ip = request.getRemoteAddr();
            ips.add(ip);
        }
        LOGGER.info(MessageFormat.format("获取客户端请求IP：,IP为[{0}]", ips));
        return ips.get(0);
    }

    private static boolean isIpLegal(String ip) {
        return ip != null && ip.length() > 0 && !("unknown".equalsIgnoreCase(ip));
    }

    /**
     * 获得客户referer
     */
    public static String getClientReferer(HttpServletRequest request) {
        return request.getHeader(FrontendWebConstants.HEADER_REFFER);
    }

}
