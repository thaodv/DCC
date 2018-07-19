package io.wexchain.dcc.service.frontend.filter;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by yy on 2018/1/9.
 */
public class I2fCommonsMultipartResolver extends CommonsMultipartResolver {

    protected CommonsFileUploadSupport.MultipartParsingResult parseRequest(HttpServletRequest request)throws MultipartException {
        String encoding = determineEncoding(request);
        FileUpload fileUpload = this.prepareFileUpload(encoding,request);
        try {
            List fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
            return parseFileItems(fileItems, encoding);

        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new ErrorCodeException(FrontendErrorCode.MAX_UPLOAD_SIZE_EXCEEDED.name(),"上传文件不超过3MB");
        } catch (FileUploadException ex) {
            throw new ErrorCodeException(FrontendErrorCode.UPLOAD_METHOD_NOT_SUPPORT.name(),"上传方式不支持");
        }

    }

    protected FileUpload prepareFileUpload(String encoding,HttpServletRequest request) {
        FileUpload fileUpload = getFileUpload();
        FileUpload actualFileUpload = fileUpload;
        // Use new temporary FileUpload instance if the request specifies
        // its own encoding that does not match the default encoding.
        if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
            actualFileUpload = newFileUpload(getFileItemFactory());
            actualFileUpload.setHeaderEncoding(encoding);
            actualFileUpload.setSizeMax(1024 * 1024 * 3);//重新设置文件限制5M
        }
        return actualFileUpload;
    }

}
