package io.wexchain.dcc.service.frontend.integration.marketing;

import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.MiningRewardRecord;
import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;

import java.math.BigDecimal;
import java.util.List;

public interface MiningRewardOperationClient {

    /**
     * 查询奖励规则
     */
    List<EcoRewardRule> queryRewardRule(QueryRewardRuleRequest request);


    /**
     * 查看挖矿贡献值
     */
    BigDecimal queryMiningContributionScore(String address);


    /**
     * 查询已获得贡献值列表
     */
    Pagination<MiningRewardRecord> queryMiningRewardRecordPage(QueryMiningRewardRecordPageRequest request);

    /**
     * 签到
     */
    BaseResponse signIn(String address);
}
