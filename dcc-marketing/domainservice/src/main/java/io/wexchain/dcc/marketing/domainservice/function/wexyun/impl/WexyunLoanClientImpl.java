package io.wexchain.dcc.marketing.domainservice.function.wexyun.impl;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.client.WexyunApiClient;
import com.wexyun.open.api.domain.member.Member;
import com.wexyun.open.api.enums.YN;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.member.MemberInfoGetByIdRequest;
import com.wexyun.open.api.request.member.MemberInfoGetByIdentityRequest;
import com.wexyun.open.api.response.QueryResponse4Single;

import io.wexchain.dcc.marketing.domainservice.function.wexyun.WexyunLoanClient;

/**
 * WexyunLoanClientImpl
 *
 * @author zhengpeng
 */
@Service
public class WexyunLoanClientImpl implements WexyunLoanClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WexyunApiClient wexyunApiClient;

    @Override
    public Member getMemberByIdentity(String identity) {
        MemberInfoGetByIdentityRequest request = new MemberInfoGetByIdentityRequest();
        request.setIdentity(identity);
        try {
            QueryResponse4Single<Member> response =  wexyunApiClient.call(request);
            if (response.isSuccess()) {
                return response.getContent();
            } else {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new ContextedRuntimeException(e);
        }
    }


    @Override
    public Member getMemberById(String memberId) {
        MemberInfoGetByIdRequest request = new MemberInfoGetByIdRequest();
        request.setRequireIdentitys(YN.Y);
        request.setMemberId(memberId);
        try {
            QueryResponse4Single<Member> response =  wexyunApiClient.call(request);
            if (response.isSuccess()) {
                return response.getContent();
            } else {
                throw new ErrorCodeException(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (WexyunClientException e) {
            throw new ContextedRuntimeException(e);
        }
    }
}
