package io.wexchain.dcc.service.frontend.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yy on 2017/9/26.
 */
public class HttpUtils {

    private static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};

    public static boolean isMSBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        for (String signal : IEBrowserSignals) {
            if (userAgent.contains(signal))
                return true;
        }
        return false;
    }
}
