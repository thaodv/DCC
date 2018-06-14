package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.EcoRewardFacade;
import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.EcoRewardStatisticsInfo;
import io.wexchain.dcc.marketing.api.model.QueryEcoRewardRuleRequest;
import io.wexchain.dcc.marketing.api.model.RewardRound;
import io.wexchain.dcc.marketing.api.model.request.CreateRewardRoundRequest;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.domainservice.EcoRewardRuleService;
import io.wexchain.dcc.marketing.domainservice.RewardRoundService;
import io.wexchain.dcc.marketing.ext.service.helper.EcoRewardRuleResponseHelper;
import io.wexchain.dcc.marketing.ext.service.helper.RewardRoundResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * EcoRewardFacadeImpl
 *
 * @author zhengpeng
 */
@Component("ecoRewardFacade")
@Validated
public class EcoRewardFacadeImpl implements EcoRewardFacade {

    @Autowired
    private EcoRewardRuleResponseHelper ecoRewardRuleResponseHelper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private EcoRewardRuleService ecoRewardRuleService;

    @Autowired
    private RewardRoundService rewardRoundService;

    @Autowired
    private RewardRoundResponseHelper rewardRoundResponseHelper;

    @Override
    public ResultResponse<RewardRound> createRewardRound(CreateRewardRoundRequest request) {
        try {
            io.wexchain.dcc.marketing.domain.RewardRound  rewardRound =
                    rewardRoundService.createRewardRound(request.getBonusDay().toDate());
            return rewardRoundResponseHelper.returnSuccess(rewardRound);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @Override
    public ListResultResponse<EcoRewardRule> queryEcoRewardRule(QueryEcoRewardRuleRequest request) {
        try {
            List<io.wexchain.dcc.marketing.domain.EcoRewardRule> result =
                    ecoRewardRuleService.queryEcoRewardRule(request);
            return ecoRewardRuleResponseHelper.returnListSuccess(result);
        } catch (Exception e) {
            return ListResultResponseUtils.exceptionListResultResponse(e);
        }
    }

    @Override
    public ResultResponse<EcoRewardStatisticsInfo> getEcoRewardStatisticsInfo(String address) {
        try {
            EcoRewardStatisticsInfo info = rewardRoundService.getEcoRewardStatisticsInfo(address);
            return ResultResponseUtils.successResultResponse(info);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
