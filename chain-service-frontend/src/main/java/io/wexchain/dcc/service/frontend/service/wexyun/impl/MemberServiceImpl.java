package io.wexchain.dcc.service.frontend.service.wexyun.impl;

import io.wexchain.dcc.service.frontend.integration.wexyun.MemberOperationClient;
import io.wexchain.dcc.service.frontend.model.param.RegisterParam;
import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;
import io.wexchain.dcc.service.frontend.service.wexyun.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 */
@Service(value = "memberService")
public class MemberServiceImpl implements MemberService{

    @Value(value = "${default.member.pwd}")
    private String defaultPwd;

    @Autowired
    private MemberOperationClient memberOperationClient;

    @Override
    public String register(RegisterRequest request) {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setLoginPwd(defaultPwd);
        registerParam.setLoginName(request.getLoginName());
        return memberOperationClient.register(registerParam);
    }

    @Override
    public String loginPasswordCheck(String loginName) {
        return memberOperationClient.loginPasswordCheck(loginName);
    }

    @Override
    public String getByIdentity(String loginName) {
        return memberOperationClient.getByIdentity(loginName);
    }

    public String getDefaultPwd() {
        return defaultPwd;
    }

    public void setDefaultPwd(String defaultPwd) {
        this.defaultPwd = defaultPwd;
    }
}
