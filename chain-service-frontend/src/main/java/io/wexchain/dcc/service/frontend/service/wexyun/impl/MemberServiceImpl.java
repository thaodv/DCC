package io.wexchain.dcc.service.frontend.service.wexyun.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.ctrlr.security.MemberDetails;
import io.wexchain.dcc.service.frontend.integration.message.impl.LoginRouter;
import io.wexchain.dcc.service.frontend.integration.wexyun.MemberOperationClient;
import io.wexchain.dcc.service.frontend.model.param.RegisterParam;
import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;
import io.wexchain.dcc.service.frontend.service.dcc.cert.CertService;
import io.wexchain.dcc.service.frontend.service.wexyun.MemberService;
import io.wexchain.notify.domain.dcc.LoginEvent;
import io.wexchain.notify.domain.dcc.MessageBusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
@Service(value = "memberService")
public class MemberServiceImpl implements MemberService{

    @Value(value = "${default.member.pwd}")
    private String defaultPwd;

    @Autowired
    private MemberOperationClient memberOperationClient;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private CertService certService;

    @Autowired
    private LoginRouter loginRouter;

    @Override
    public String register(RegisterRequest request) {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setLoginPwd(defaultPwd);
        registerParam.setLoginName(request.getLoginName());
        registerParam.setInviteCode(request.getInviteCode());
        return memberOperationClient.register(registerParam);
    }

    @Override
    public String registerAndLogin(RegisterRequest registerRequest, HttpServletRequest request) {

        String memberId = register(registerRequest);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        MemberDetails memberDetails = new MemberDetails(Long.parseLong(memberId), registerRequest.getLoginName(), "", true, authorities);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken (memberDetails, "");
        try{
            token.setDetails(memberDetails);
            Authentication authenticatedUser = authenticationManager
                    .authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        }
        catch( Exception e ){
            throw new ErrorCodeException(FrontendErrorCode.LOGIN_FAILURE.name(),FrontendErrorCode.LOGIN_FAILURE.getDescription());
        }
        try {
            loginRouter.route(new LoginEvent(registerRequest.getAddress(), new Date()));
        }catch (Exception e){
        }
        return memberId;

    }


    @Override
    public String loginPasswordCheck(String loginName) {
        return memberOperationClient.loginPasswordCheck(loginName);
    }

    @Override
    public Member getByIdentity(String loginName) {
        Member member = memberOperationClient.getByIdentity(loginName);
        if(member != null){
            try{
                certService.validateId(loginName);
            }catch (ErrorCodeException e){
                member.setInviteCode(null);
            }
        }
        return member;
    }

    @Override
    public List<Member> queryByInvited(String memberId) {
        return memberOperationClient.queryByInvited(memberId);
    }

    public String getDefaultPwd() {
        return defaultPwd;
    }

    public void setDefaultPwd(String defaultPwd) {
        this.defaultPwd = defaultPwd;
    }
}
