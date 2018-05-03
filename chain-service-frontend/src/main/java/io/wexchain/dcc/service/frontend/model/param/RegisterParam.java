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
