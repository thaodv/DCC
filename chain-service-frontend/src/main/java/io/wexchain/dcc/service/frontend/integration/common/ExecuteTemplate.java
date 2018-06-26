package io.wexchain.dcc.service.frontend.integration.common;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.common.exception.SystemErrorCodeException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

/**
 * <p>执行器模板，记录调用日志</p>
 * @author yanyi
 */
public class ExecuteTemplate {

    /**
     * 执行
     */
    public static <T> T execute(Invoker<T> invoker, Logger logger, String logMessage,
                                Object... parms) {
        StopWatch watch = new StopWatch();
        watch.start();
        logger.info("clearing-frontend => remoteServer,msg={},request parms={}", logMessage,
            JsonUtil.toJson(parms));
        T t = null;
        try {
            t = invoker.excute();
            watch.stop();
            logger.info("clearing-frontend <= remoteServer,msg={},timeCost={}ms,response={}",
                    logMessage, watch.getTime(), JsonUtil.toJson(t));
        } catch (Exception e) {
            logger.info("clearing-frontend <= remoteServer,msg={},throws Exception", logMessage, e);
            throw new SystemErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(), "系统异常");
        }
        return t;
    }
}
