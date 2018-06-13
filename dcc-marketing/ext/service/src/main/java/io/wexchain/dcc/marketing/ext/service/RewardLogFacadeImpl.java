package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.RewardLogFacade;
import io.wexchain.dcc.marketing.api.model.RewardLog;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.marketing.domainservice.RewardLogService;
import io.wexchain.dcc.marketing.ext.service.helper.RewardLogResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * RewardLogFacadeImpl
 *
 * @author zhengpeng
 */
@Component("rewardLogFacade")
@Validated
public class RewardLogFacadeImpl implements RewardLogFacade {

    @Autowired
    private RewardLogResponseHelper rewardLogResponseHelper;

    @Autowired
    private RewardLogService rewardLogService;

    @Override
    public ResultResponse<Pagination<RewardLog>> queryRewardLogPage(QueryRewardLogPageRequest request) {
        try {
            Page<io.wexchain.dcc.marketing.domain.RewardLog> pageResult = rewardLogService
                    .queryRewardLogPage(request);
            return rewardLogResponseHelper.returnPageSuccess(pageResult);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @Override
    public ResultResponse<BigDecimal> getTotalRewardAmount(GetTotalRewardAmountRequest request) {
        try {
            BigDecimal amount = rewardLogService.getTotalRewardAmount(request);
            return ResultResponseUtils.successResultResponse(amount);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
