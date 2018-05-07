package io.wexchain.dcc.service.frontend.integration.wexyun.impl;

import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.weihui.finance.common.credit2.enums.ApplySourceType;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.client.DefaultWexyunApiClient;
import com.wexyun.open.api.domain.credit2.Credit2Apply;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.enums.credit2.MemberIdType;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.credit.credit2.Credit2ApplyPageGetRequest;
import com.wexyun.open.api.response.QueryResponse4Batch;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.wexyun.Credit2OperationClient;
import io.wexchain.dcc.service.frontend.service.wexyun.impl.Credit2ApplyAddRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yanyi
 */
@Component
public class DefaultCredit2OperationClient implements Credit2OperationClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCredit2OperationClient.class);

    public static final String BORROW_USE = "数字资产借贷";

    @Autowired
    private DefaultWexyunApiClient wexyunApiClient;

    @Override
    public Long apply(Credit2ApplyAddRequest credit2ApplyAddRequest) {
        credit2ApplyAddRequest.setApplySourceType(ApplySourceType.TOKENCOIN);
        credit2ApplyAddRequest.setBorrowUse(BORROW_USE);
        credit2ApplyAddRequest.setMemberIdType(MemberIdType.ZJTG_UID);

        try {
            LOGGER.info("进件请求,请求参数：{}",JsonUtil.toJson(credit2ApplyAddRequest));
            QueryResponse4Single<Credit2ApplyAddResult> response  = wexyunApiClient.call(credit2ApplyAddRequest);
            LOGGER.info("进件请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(credit2ApplyAddRequest),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getContent().getApplyId();
            }
            throw new ErrorCodeException(FrontendErrorCode.LOAN_CREDIT_FAIL.name(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("进件请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }

    @Override
    public List<Credit2Apply> pageGet(List<String> externalApplyIdList) {
        Credit2ApplyPageGetRequest request = new Credit2ApplyPageGetRequest();
        request.setExternalApplyIdList(externalApplyIdList);
        try {
            LOGGER.info("信贷分页查询请求,请求参数：{}",JsonUtil.toJson(request));
            QueryResponse4Batch<Credit2Apply> response  = wexyunApiClient.call(request);
            LOGGER.info("信贷分页查询请求结果,请求参数：{}，响应参数：{}",JsonUtil.toJson(request),JsonUtil.toJson(response));
            if(response.isSuccess()){
                return response.getItems();
            }
            throw new ErrorCodeException(FrontendErrorCode.QUERY_LOAN_CREDIT_FAIL.name(),response.getResponseMessage());
        } catch (WexyunClientException e) {
            LOGGER.error("信贷分页查询请求访问开放平台错误:{}",e);
            throw new ErrorCodeException(FrontendErrorCode.SYSTEM_ERROR.name(),"远程系统异常");
        }
    }
}
