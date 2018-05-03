package io.wexchain.dcc.service.frontend.ctrlr.security;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yy on 2017/7/27.
 */
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private final HttpStatus httpStatusToReturn;

    public LogoutSuccessHandler(HttpStatus httpStatusToReturn) {
        Assert.notNull(httpStatusToReturn, "The provided HttpStatus must not be null.");
        this.httpStatusToReturn = httpStatusToReturn;
    }

    public LogoutSuccessHandler() {
        this.httpStatusToReturn = HttpStatus.OK;
    }
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.setStatus(this.httpStatusToReturn.value());
        response.setContentType("application/json;charset=utf8");
        response.getWriter().print(
                JsonUtil.toJson(BaseResponseUtils.successBaseResponse("退出登录成功")));
    }
}
