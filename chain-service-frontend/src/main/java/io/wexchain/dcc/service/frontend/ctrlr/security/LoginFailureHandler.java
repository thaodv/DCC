package io.wexchain.dcc.service.frontend.ctrlr.security;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

	    response.setContentType("application/json;charset=utf8");
		response.getWriter().print(
				JsonUtil.toJson(BaseResponseUtils.codeBaseResponse(SystemCode.SUCCESS,
						FrontendErrorCode.LOGIN_FAILURE.name(),
						FrontendErrorCode.LOGIN_FAILURE.getDescription())));
	}
}
