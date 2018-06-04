package io.wexchain.dcc.service.frontend.model.param;


/**
 * <p>
 * 创建会员请求参数
 * </p>
 * 
 */
public class RegisterParam {

    /**
     * 登录名
     */
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
