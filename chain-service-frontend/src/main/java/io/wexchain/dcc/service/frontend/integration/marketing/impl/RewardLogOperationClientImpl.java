package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.dcc.marketing.api.facade.RewardLogFacade;
import io.wexchain.dcc.marketing.api.model.RewardLog;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.RewardLogOperationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
public class RewardLogOperationClientImpl implements RewardLogOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "rewardLogFacade")
    private IntegrationProxy<RewardLogFacade> rewardLogFacade;

    @Override
    public Pagination<RewardLog> queryRewardLogPage(QueryRewardLogPageRequest request) {
        ResultResponse<Pagination<RewardLog>> resultResponse = ExecuteTemplate.execute(() ->
                rewardLogFacade.buildInst().queryRewardLogPage(request), logger, "查询奖励记录", request);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.QUERY_REWARD_LOG_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }

    @Override
    public BigDecimal getTotalRewardAmount(GetTotalRewardAmountRequest request) {
        ResultResponse<BigDecimal> resultResponse = ExecuteTemplate.execute(() ->
                rewardLogFacade.buildInst().getTotalRewardAmount(request), logger, "查询用户奖励金额", request);
        ErrorCodeValidate.isTrue(SystemCode.SUCCESS == resultResponse.getSystemCode()
                && BusinessCode.SUCCESS.name().equals(resultResponse.getBusinessCode()), FrontendErrorCode.GET_TOTAL_REWARD_AMOUNT_FAIL, resultResponse.getMessage());
        return resultResponse.getResult();
    }
}
