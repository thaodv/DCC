package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.MiningRewardFacade;
import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.MiningRewardRecord;
import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.MiningRewardOperationClient;
import io.wexchain.dcc.service.frontend.utils.ResultResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Component
public class MiningRewardOperationClientImpl implements MiningRewardOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "miningRewardFacade")
    private IntegrationProxy<MiningRewardFacade> miningRewardFacade;

    @Override
    public List<EcoRewardRule> queryRewardRule(QueryRewardRuleRequest request) {
        ListResultResponse<EcoRewardRule> resultResponse = ExecuteTemplate.execute(() ->
                miningRewardFacade.buildInst().queryRewardRule(request), logger, "查询奖励规则", request);
        return ResultResponseValidator.getListResult(resultResponse);
    }

    @Override
    public BigDecimal queryMiningContributionScore(String address) {
        ResultResponse<BigDecimal> resultResponse = ExecuteTemplate.execute(() ->
                miningRewardFacade.buildInst().queryMiningContributionScore(address), logger, "查看挖矿贡献值", address);
        return ResultResponseValidator.getResult(resultResponse);
    }

    @Override
    public Pagination<MiningRewardRecord> queryMiningRewardRecordPage(QueryMiningRewardRecordPageRequest request) {
        ResultResponse<Pagination<MiningRewardRecord>> resultResponse = ExecuteTemplate.execute(() ->
                miningRewardFacade.buildInst().queryMiningRewardRecordPage(request), logger, "查询已获得贡献值列表", request);
        return ResultResponseValidator.getResult(resultResponse);
    }

    @Override
    public BaseResponse signIn(String address) {
       BaseResponse resultResponse = ExecuteTemplate.execute(() ->
                miningRewardFacade.buildInst().signIn(address), logger, "签到", address);
        return ResultResponseValidator.validate(resultResponse);
    }
}
