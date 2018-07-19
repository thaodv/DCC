package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.EcoRewardFacade;
import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.EcoRewardStatisticsInfo;
import io.wexchain.dcc.marketing.api.model.QueryEcoRewardRuleRequest;
import io.wexchain.dcc.marketing.api.model.RewardRound;
import io.wexchain.dcc.marketing.api.model.request.CreateRewardRoundRequest;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.EcoRewardOperationClient;
import io.wexchain.dcc.service.frontend.utils.ResultResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class EcoRewardOperationClientImpl implements EcoRewardOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "ecoRewardFacade")
    private IntegrationProxy<EcoRewardFacade> ecoRewardFacade;

    @Override
    public List<EcoRewardRule> queryEcoRewardRule(QueryEcoRewardRuleRequest request) {
        ListResultResponse<EcoRewardRule> resultResponse = ExecuteTemplate.execute(() ->
                ecoRewardFacade.buildInst().queryEcoRewardRule(request), logger, "查询奖励规则", request);
        return ResultResponseValidator.getListResult(resultResponse);
    }

    @Override
    public EcoRewardStatisticsInfo getEcoRewardStatisticsInfo(String address) {
        ResultResponse<EcoRewardStatisticsInfo> resultResponse = ExecuteTemplate.execute(() ->
                ecoRewardFacade.buildInst().getEcoRewardStatisticsInfo(address), logger, "查询生态奖励统计", address);
        return ResultResponseValidator.getResult(resultResponse);
    }

    @Override
    public RewardRound createRewardRound(CreateRewardRoundRequest request) {
        ResultResponse<RewardRound> resultResponse = ExecuteTemplate.execute(() ->
                ecoRewardFacade.buildInst().createRewardRound(request), logger, "创建奖励轮次", request);
        return ResultResponseValidator.getResult(resultResponse);
    }

}
