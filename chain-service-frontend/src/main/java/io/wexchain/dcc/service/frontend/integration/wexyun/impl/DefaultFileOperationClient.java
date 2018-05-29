package io.wexchain.dcc.service.frontend.integration.wexyun.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.client.DefaultWexyunApiClient;
import com.wexyun.open.api.domain.file.DownloadFileInfo;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.BaseFileDownLoadRequest;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.wexyun.FileOperationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author yanyi
 */
@Component
public class DefaultFileOperationClient implements FileOperationClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileOperationClient.class);

    @Autowired
    private DefaultWexyunApiClient wexyunApiClient;

    @Override
    public InputStream download(String filePath) {
        try {
            BaseFileDownLoadRequest request = new BaseFileDownLoadRequest();
            request.setFilePath(filePath);
            DownloadFileInfo downloadFileInfo = wexyunApiClient.downLoad(request);
            if(downloadFileInfo != null && downloadFileInfo.getInputStream() != null){
                return downloadFileInfo.getInputStream();
            }
            throw new ErrorCodeException(FrontendErrorCode.DOWNLOAD_FAIL.name(),"文件下载错误");
        } catch (WexyunClientException e) {
            LOGGER.error("下载文件请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }

    }
}
