package io.wexchain.dcc.service.frontend.ctrlr;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;

public class BaseController {

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	public BaseResponse others(ConstraintViolationException e) {
		Iterator<ConstraintViolation<?>> iterator = e.getConstraintViolations().iterator();
		return BaseResponseUtils.codeBaseResponse(
				SystemCode.SUCCESS, FrontendErrorCode.ILLEGAL_ARGUMENT.name(), iterator.next().getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public BaseResponse others(Exception e) {
		return BaseResponseUtils.exceptionBaseResponse(e);
	}
}
