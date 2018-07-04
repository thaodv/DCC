package io.wexchain.dcc.service.frontend.ctrlr.security;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import io.wexchain.dcc.service.frontend.integration.message.impl.LoginRouter;
import io.wexchain.notify.domain.dcc.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {


	@Autowired
	private LoginRouter loginRouter;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		String address = request.getParameter("address");
		try {
			loginRouter.route(new LoginEvent(address, new Date()));
		}catch (Exception e){
		}
		response.setContentType("application/json;charset=utf8");
		response.getWriter().print(
				JsonUtil.toJson(BaseResponseUtils.successBaseResponse("登录成功")));

	}

}
