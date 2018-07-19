package io.wexchain.dcc.service.frontend.service.marketing.impl;

import com.google.common.collect.Lists;
import com.wexmarket.topia.commons.basic.rpc.utils.PaginationUtils;
import com.wexmarket.topia.commons.pagination.*;
import io.wexchain.dcc.marketing.api.constant.ParticipatorRole;
import io.wexchain.dcc.marketing.api.model.*;
import io.wexchain.dcc.marketing.api.model.request.CreateRewardRoundRequest;
import io.wexchain.dcc.marketing.api.model.request.GetTotalRewardAmountRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardLogPageRequest;
import io.wexchain.dcc.service.frontend.common.convertor.RewardConvertor;
import io.wexchain.dcc.service.frontend.integration.marketing.EcoRewardOperationClient;
import io.wexchain.dcc.service.frontend.integration.marketing.RewardLogOperationClient;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.YesterdayEcoBonusVo;
import io.wexchain.dcc.service.frontend.service.marketing.EcoService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * EcoServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class EcoServiceImpl implements EcoService {

    private Logger logger = LoggerFactory.getLogger(EcoServiceImpl.class);

    @Autowired
    private RewardLogOperationClient rewardLogOperationClient;

    @Autowired
    private EcoRewardOperationClient ecoRewardOperationClient;

    @Override
    public Pagination<EcoBonusVo> queryBonus(PageParam pageParam,String address) {
        QueryRewardLogPageRequest queryRewardLogPageRequest = new QueryRewardLogPageRequest();
        queryRewardLogPageRequest.setActivityCode("10003");
        queryRewardLogPageRequest.setAddress(address);
        queryRewardLogPageRequest.setSortPageParam(new SortPageParam(pageParam.getNumber(),pageParam.getSize(),Collections.singletonList(new SortParam(Direction.DESC,"createdTime"))));
        Pagination<RewardLog> rewardLogPage = rewardLogOperationClient.queryRewardLogPage(queryRewardLogPageRequest);
        Pagination<EcoBonusVo> ecoBonusVos = PaginationUtils.transform(rewardLogPage, from -> RewardConvertor.convertEcoBonusVo(from));
        return ecoBonusVos;
    }

    @Override
    public BigDecimal getTotalEcoBonus(String address) {
        GetTotalRewardAmountRequest getTotalRewardAmountRequest = new GetTotalRewardAmountRequest();
        getTotalRewardAmountRequest.setActivityCode("10003");
        getTotalRewardAmountRequest.setAddress(address);
        return rewardLogOperationClient.getTotalRewardAmount(getTotalRewardAmountRequest);
    }

    public YesterdayEcoBonusVo getYesterdayBonus(String address) {

        EcoRewardStatisticsInfo ecoRewardStatisticsInfo = ecoRewardOperationClient.getEcoRewardStatisticsInfo(address);
        YesterdayEcoBonusVo yesterdayEcoBonusVo = new YesterdayEcoBonusVo();
        yesterdayEcoBonusVo.setYesterdayAmount(ecoRewardStatisticsInfo.getYesterdayScore());
        yesterdayEcoBonusVo.setAmount(ecoRewardStatisticsInfo.getYesterdayAmount());
        return yesterdayEcoBonusVo;
    }

    @Override
    public List<EcoBonusRuleVo> queryBonusRule() {
        QueryEcoRewardRuleRequest queryEcoRewardRuleRequest = new QueryEcoRewardRuleRequest();
        queryEcoRewardRuleRequest.setParticipatorRole(ParticipatorRole.USER);
        List<EcoRewardRule> ecoRewardRules = ecoRewardOperationClient.queryEcoRewardRule(queryEcoRewardRuleRequest);
        if (ecoRewardRules != null){
            List<EcoBonusRuleVo> transform = Lists.transform(ecoRewardRules, from -> RewardConvertor.convertEcoBonusRuleVo(from));
            return transform;
        }
        return null;
    }

    @Override
    public RewardRound createRewardRound(Date bonusDay) {
        CreateRewardRoundRequest createRewardRoundRequest = new CreateRewardRoundRequest();
        createRewardRoundRequest.setBonusDay(new DateTime(bonusDay));
        return ecoRewardOperationClient.createRewardRound(createRewardRoundRequest);
    }
}
