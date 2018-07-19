package io.wexchain.dcc.service.frontend.integration.wexyun.impl;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.client.DefaultWexyunApiClient;
import com.wexyun.open.api.domain.member.LoginPwsCheckResult;
import com.wexyun.open.api.domain.member.Member;
import com.wexyun.open.api.enums.IdentityType;
import com.wexyun.open.api.enums.LoginNameType;
import com.wexyun.open.api.enums.YN;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.member.*;
import com.wexyun.open.api.request.member.inner.InnerMemberLoginPasswordCheckRequest;
import com.wexyun.open.api.request.member.inner.InnerPersonalMemberInfoAddRequest;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.CreateMemberResponse;
import com.wexyun.open.api.response.QueryResponse4Batch;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.wexyun.MemberOperationClient;
import io.wexchain.dcc.service.frontend.model.param.RegisterParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        memberInfoAddRequest.setInviteCode(request.getInviteCode());
        try {
            LOGGER.info("创建会员请求,请求参数：{}",JsonUtil.toJson(memberInfoAddRequest));
            QueryResponse4Single<CreateMemberResponse> response  = wexyunApiClient.call(memberInfoAddRequest);
            LOGGER.info("创建会员请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(memberInfoAddRequest),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getContent().getMemberId();
            }
            throw new ErrorCodeException(response.getResponseCode(),response.getResponseMessage());
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
    public Member getByIdentity(String loginName) {
        MemberInfoGetByIdentityRequest getByIdentityRequest = new MemberInfoGetByIdentityRequest();
        getByIdentityRequest.setIdentity(loginName);
        try {
            LOGGER.info("通过会员标示查询会员信息请求,请求参数：{}",JsonUtil.toJson(getByIdentityRequest));
            QueryResponse4Single<Member> response  = wexyunApiClient.call(getByIdentityRequest);
            LOGGER.info("通过会员标示查询会员信息请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(getByIdentityRequest),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getContent();
            }
            throw new ErrorCodeException(response.getResponseCode(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("通过会员标示查询会员信息请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

    @Override
    public void identityUpd(String loginName,String memberId,String newIdentity) {
        MemberIdentityUpdateRequest request = new MemberIdentityUpdateRequest();
        request.setMemberId(memberId);
        request.setIdentity(newIdentity);
        request.setIdentityType(IdentityType.CHARACTER);
        try {
            LOGGER.info("更新会员标识请求,请求参数：{}",JsonUtil.toJson(request));
            BaseResponse response  = wexyunApiClient.call(request);
            LOGGER.info("更新会员标识请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(request),JsonUtil.toJson(response));
            if(response.isSuccess()){
            }
            throw new ErrorCodeException(response.getResponseCode(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("更新会员标识请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

    @Override
    public List<Member> queryByInvited(String memberId) {
        InvitedMemberInfosGetRequest request = new InvitedMemberInfosGetRequest();
        request.setMemberId(memberId);
        request.setRequireIdentitys(YN.Y);
        request.setPageNo(1);
        request.setPageSize(Integer.MAX_VALUE);
        try {
            LOGGER.info("通过邀请码查询会员列表请求,请求参数：{}",JsonUtil.toJson(request));
            QueryResponse4Batch<Member> response  = wexyunApiClient.call(request);
            LOGGER.info("通过邀请码查询会员列表请求,请求参数：{}，响应参数：{}",JsonUtil.toJson(request),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getItems();
            }
            throw new ErrorCodeException(response.getResponseCode(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("通过邀请码查询会员列表请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

    @Override
    public List<Member> queryInfoList() {
        MemberInfoListRequest request = new MemberInfoListRequest();
        request.setPageNo(1);
        request.setPageSize(Integer.MAX_VALUE);
        try {
            LOGGER.info("通过邀请码查询会员列表请求,请求参数：{}",JsonUtil.toJson(request));
            QueryResponse4Batch<Member> response  = wexyunApiClient.call(request);
            LOGGER.info("通过邀请码查询会员列表请求,请求参数：{}，响应参数：{}",JsonUtil.toJson(request),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getItems();
            }
            throw new ErrorCodeException(response.getResponseCode(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("通过邀请码查询会员列表请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

}
