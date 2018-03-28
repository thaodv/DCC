package io.wexchain.passport.chain.observer.common.util;

import java.util.UUID;

/**
 * Created by yy on 2017/6/20.
 */
public class UuidUtils {

    public static String getReqNo(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

}
