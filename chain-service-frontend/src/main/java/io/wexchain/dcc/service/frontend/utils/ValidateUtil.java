package io.wexchain.dcc.service.frontend.utils;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 校验空工具类
 * </p>
 *
 * @author yanyi
 * @version $1: ValidateUtil.java, v 0.1 2017-02-15 上午11:56:19 yanyi Exp $
 */
public class ValidateUtil {

    private ValidateUtil() {
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }

        if (o instanceof String) {
            return o.toString().trim().isEmpty();
        }

        if (o instanceof Collection) {
            return ((Collection) o).isEmpty();
        }

        if (o instanceof Map) {
            return ((Map) o).isEmpty();
        }

        if (o.getClass().isArray()) {
            return ((Object[]) o).length == 0;
        }

        return false;
    }
}
