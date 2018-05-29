package io.wexchain.dcc.service.frontend.uploaderhelper;

import com.weihui.finance.common.credit2.domain.result.UploadChainNotifyResult;
import com.weihui.finance.common.credit2.enums.UploadChainBusinessStatus;
import com.weihui.mq.handler.notify.AbstractNotifyMessageHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wuxinxin on 2018/5/7.
 */
public class FlowExecuteMessageHandler extends AbstractNotifyMessageHandler {

    private static Log log = LogFactory.getLog(FlowExecuteMessageHandler.class);

    private String genericClass;

    public void setGenericClass(String genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void handleMessage(Object request) throws Exception {
        System.out.println("收到message");
        UploadChainNotifyResult uploadChainNotifyResult=(UploadChainNotifyResult)request;

        UploadChainBusinessStatus businessStatus = uploadChainNotifyResult.getBusinessStatus();
        String outOrderId = uploadChainNotifyResult.getOutOrderId();

        System.out.println("=================");
        System.out.println("businessStatus:"+businessStatus.getMessage());
        System.out.println("outOrderId:"+outOrderId);
        System.out.println("=================");
    }

}
