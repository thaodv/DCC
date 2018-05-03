package io.wexchain.dcc.service.frontend.integration.wexyun.impl;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.client.DefaultWexyunApiClient;
import com.wexyun.open.api.domain.member.LoginPwsCheckResult;
import com.wexyun.open.api.domain.member.Member;
import com.wexyun.open.api.enums.LoginNameType;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.member.MemberInfoGetByIdentityRequest;
import com.wexyun.open.api.request.member.inner.InnerMemberLoginPasswordCheckRequest;
import com.wexyun.open.api.request.member.inner.InnerPersonalMemberInfoAddRequest;
import com.wexyun.open.api.response.CreateMemberResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.wexyun.MemberOperationClient;
import io.wexchain.dcc.service.frontend.model.param.RegisterParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yanyi
 */
@Component
public class DefaultMemberOperationClient implements MemberOperationClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMemberOperationClient.class);

    @Autowired
    private DefaultWexyunApiClient wexyunApiClient;

    @Override
    public String register(RegisterParam request) {
        InnerPersonalMemberInfoAddRequest memberInfoAddRequest = new InnerPersonalMemberInfoAddRequest();
        memberInfoAddRequest.setLoginName(request.getLoginName());
        memberInfoAddRequest.setLoginNameType(LoginNameType.CHARACTER);
        memberInfoAddRequest.setLoginPwd(request.getLoginPwd());
        try {
            LOGGER.info("创建会员请求,请求参数：{}",JsonUtil.toJson(memberInfoAddRequest));
            QueryResponse4Single<CreateMemberResponse> response  = wexyunApiClient.call(memberInfoAddRequest);
            LOGGER.info("创建会员请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(memberInfoAddRequest),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getContent().getMemberId();
            }
            throw new ErrorCodeException(FrontendErrorCode.REGISTER_FAIL.name(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("创建会员请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

    @Override
    public String loginPasswordCheck(String loginName) {
        InnerMemberLoginPasswordCheckRequest checkRequest = new InnerMemberLoginPasswordCheckRequest();
        checkRequest.setLoginName(loginName);
        try {
            LOGGER.info("检查会员密码请求,请求参数：{}",JsonUtil.toJson(checkRequest));
            QueryResponse4Single<LoginPwsCheckResult> response  = wexyunApiClient.call(checkRequest);
            LOGGER.info("检查会员密码请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(checkRequest),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getContent().getReturnCode().name();
            }
            throw new ErrorCodeException(FrontendErrorCode.REGISTER_FAIL.name(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("检查会员密码请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

    @Override
    public String getByIdentity(String loginName) {
        MemberInfoGetByIdentityRequest getByIdentityRequest = new MemberInfoGetByIdentityRequest();
        getByIdentityRequest.setIdentity(loginName);
        try {
            LOGGER.info("通过会员标示查询会员信息请求,请求参数：{}",JsonUtil.toJson(getByIdentityRequest));
            QueryResponse4Single<Member> response  = wexyunApiClient.call(getByIdentityRequest);
            LOGGER.info("通过会员标示查询会员信息请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(getByIdentityRequest),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getContent() != null ? response.getContent().getMemberId() : null;
            }
            throw new ErrorCodeException(FrontendErrorCode.REGISTER_FAIL.name(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("通过会员标示查询会员信息请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }
}
