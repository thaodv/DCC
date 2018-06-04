package io.wexchain.dcc.service.frontend.model.request;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 创建会员请求参数
 * </p>
 * 
 */
public class RegisterRequest extends AuthenticationRequest{

    /**
     * 登录名
     */
    @NotBlank(message = "登录名不能为空")
    private String        loginName;

    private String        loginPwd;

    private String        inviteCode;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

}
