package io.wexchain.dcc.service.frontend.integration.marketing;

import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.dcc.marketing.api.model.RewardLog;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;

import java.math.BigDecimal;

public interface RewardLogOperationClient {

    /**
     * 查询奖励记录
     */
    Pagination<RewardLog> queryRewardLogPage(QueryRewardLogPageRequest request);


    /**
     * 查询用户奖励金额
     */
    BigDecimal getTotalRewardAmount(GetTotalRewardAmountRequest request);

}
