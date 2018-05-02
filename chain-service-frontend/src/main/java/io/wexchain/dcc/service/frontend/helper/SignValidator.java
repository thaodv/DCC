package io.wexchain.dcc.service.frontend.helper;


import io.wexchain.dcc.service.frontend.model.request.SignBaseRequest;

/**
 * 图像认证服务
 * @author yanyi
 */

public interface SignValidator {

    boolean validateSign(SignBaseRequest request);
}
