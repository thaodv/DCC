package io.wexchain.dcc.service.frontend.utils;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.common.exception.SystemErrorCodeException;

import java.util.List;

/**
 * ResultResponseValidator
 *
 * @author zhengpeng
 */
public class ResultResponseValidator {

    public static <T> T getResult(ResultResponse<T> resultResponse) {
        if (resultResponse.getSystemCode() != SystemCode.SUCCESS) {
            throw new SystemErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(), "系统错误");
        }
        if (!resultResponse.getBusinessCode().equals("SUCCESS")) {
            throw new ErrorCodeException(resultResponse.getBusinessCode(), resultResponse.getMessage());
        }
        return resultResponse.getResult();
    }

    public static <T> List<T> getListResult(ListResultResponse<T> resultResponse) {
        if (resultResponse.getSystemCode() != SystemCode.SUCCESS) {
            throw new SystemErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(), "系统错误");
        }
        if (!resultResponse.getBusinessCode().equals("SUCCESS")) {
            throw new ErrorCodeException(resultResponse.getBusinessCode(), resultResponse.getMessage());
        }
        return resultResponse.getResultList();
    }

    public static BaseResponse validate(BaseResponse baseResponse) {
        if (baseResponse.getSystemCode() != SystemCode.SUCCESS) {
            throw new SystemErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(), "系统错误");
        }
        if (!baseResponse.getBusinessCode().equals("SUCCESS")) {
            throw new ErrorCodeException(baseResponse.getBusinessCode(), baseResponse.getMessage());
        }
        return baseResponse;
    }
}
