package io.wexchain.dcc.service.frontend.service.marketing.impl;

import com.google.common.collect.Lists;
import com.wexmarket.topia.commons.basic.rpc.utils.PaginationUtils;
import com.wexmarket.topia.commons.pagination.*;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import io.wexchain.dcc.marketing.api.constant.ParticipatorRole;
import io.wexchain.dcc.marketing.api.model.*;
import io.wexchain.dcc.marketing.api.model.request.*;
import io.wexchain.dcc.service.frontend.common.convertor.RewardConvertor;
import io.wexchain.dcc.service.frontend.integration.marketing.MiningRewardOperationClient;
import io.wexchain.dcc.service.frontend.integration.marketing.RewardLogOperationClient;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.MiningRewardRecordVo;
import io.wexchain.dcc.service.frontend.service.marketing.MiningRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * ScenarioServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class MiningRewardServiceImpl implements MiningRewardService {

    @Resource
    private MiningRewardOperationClient miningRewardOperationClient;

    @Autowired
    private RewardLogOperationClient rewardLogOperationClient;

    @Override
    public List<EcoBonusRuleVo> queryRewardRule() {
        QueryRewardRuleRequest queryRewardRuleRequest = new QueryRewardRuleRequest();
        queryRewardRuleRequest.setActivityCode("10004");
        queryRewardRuleRequest.setParticipatorRole(ParticipatorRole.USER);
        List<EcoRewardRule> ecoRewardRules = miningRewardOperationClient.queryRewardRule(queryRewardRuleRequest);
        if (ecoRewardRules != null){
            List<EcoBonusRuleVo> transform = Lists.transform(ecoRewardRules, from -> RewardConvertor.convertEcoBonusRuleVo(from));
            return transform;
        }
        return null;
    }

    @Override
    public BigDecimal queryMiningContributionScore(String address) {
        return miningRewardOperationClient.queryMiningContributionScore(address);
    }

    @Override
    public Pagination<MiningRewardRecordVo> queryMiningRewardRecordPage(PageParam pageParam, String address) {
        QueryMiningRewardRecordPageRequest queryMiningRewardRecordPageRequest = new QueryMiningRewardRecordPageRequest();
        queryMiningRewardRecordPageRequest.setSortPageParam(new SortPageParam(pageParam.getNumber(),pageParam.getSize(),new SortParam(Direction.DESC,"createdTime")));
        queryMiningRewardRecordPageRequest.setAddress(address);

        Pagination<MiningRewardRecord> miningRewardRecordPagination = miningRewardOperationClient.queryMiningRewardRecordPage(queryMiningRewardRecordPageRequest);
        return PaginationUtils.transform(miningRewardRecordPagination, from -> RewardConvertor.convertMiningRewardRecordVo(from));
    }

    @Override
    public Pagination<EcoBonusVo> queryBonus(PageParam pageParam, String address) {
        QueryRewardLogPageRequest queryRewardLogPageRequest = new QueryRewardLogPageRequest();
        queryRewardLogPageRequest.setActivityCode("10004");
        queryRewardLogPageRequest.setAddress(address);
        queryRewardLogPageRequest.setSortPageParam(new SortPageParam(pageParam.getNumber(),pageParam.getSize(), Collections.singletonList(new SortParam(Direction.DESC,"createdTime"))));
        Pagination<RewardLog> rewardLogPage = rewardLogOperationClient.queryRewardLogPage(queryRewardLogPageRequest);
        Pagination<EcoBonusVo> ecoBonusVos = PaginationUtils.transform(rewardLogPage, from -> RewardConvertor.convertEcoBonusVo(from));
        return ecoBonusVos;
    }

    @Override
    public BigDecimal getTotalEcoBonus(String address) {
        GetTotalRewardAmountRequest getTotalRewardAmountRequest = new GetTotalRewardAmountRequest();
        getTotalRewardAmountRequest.setActivityCode("10004");
        getTotalRewardAmountRequest.setAddress(address);
        return rewardLogOperationClient.getTotalRewardAmount(getTotalRewardAmountRequest);
    }

    @Override
    public BaseResponse signIn(String address) {
        return miningRewardOperationClient.signIn(address);
    }
}
