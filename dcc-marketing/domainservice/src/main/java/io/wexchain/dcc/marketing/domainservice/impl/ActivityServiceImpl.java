package io.wexchain.dcc.marketing.domainservice.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.wexchain.dcc.marketing.domain.MiningRewardRecord;
import io.wexchain.dcc.marketing.domain.MiningRewardRoundItem;
import io.wexchain.dcc.marketing.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;

import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.repository.query.ActivityQueryBuilder;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * ActivityServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CandyRepository candyRepository;

    @Autowired
    private MiningRewardRecordRepository miningRewardRecordRepository;

    @Autowired
    private MiningRewardRoundItemRepository miningRewardRoundItemRepository;

    @Autowired
    private RedeemTokenRepository redeemTokenRepository;

    @Autowired
    private RewardLogRepository rewardLogRepository;

    @Override
    public Activity getActivityById(Long id) {
        return ErrorCodeValidate.notNull(
                activityRepository.findById(id).orElse(null),
                MarketingErrorCode.ACTIVITY_NOT_FOUND);
    }

    @Override
    public Activity getActivityByCode(String code) {
        return ErrorCodeValidate.notNull(
                activityRepository.findByCode(code),
                MarketingErrorCode.ACTIVITY_NOT_FOUND);
    }

    @Override
    public List<Activity> queryActivity(QueryActivityRequest request) {
        if (CollectionUtils.isNotEmpty(request.getStatusList())) {
            request.getStatusList().remove(ActivityStatus.CREATED);
        } else {
            request.setStatusList(Arrays.asList(ActivityStatus.SHELVED,
                    ActivityStatus.STARTED, ActivityStatus.ENDED));
        }

        List<Activity> activityList = activityRepository.findAll(
                ActivityQueryBuilder.query(request),
                Sort.by(Sort.Order.desc("startTime"), Sort.Order.asc("name")));
        List<Activity> startedList = new ArrayList<>();
        List<Activity> shelvedList = new ArrayList<>();
        List<Activity> endedList = new ArrayList<>();
        activityList.forEach(act -> {
            if (act.getStatus() == ActivityStatus.STARTED) {
                startedList.add(act);
            }
            if (act.getStatus() == ActivityStatus.SHELVED) {
                shelvedList.add(act);
            }
            if (act.getStatus() == ActivityStatus.ENDED) {
                endedList.add(act);
            }
        });

        List<Activity> result = new ArrayList<>(startedList);
        result.addAll(shelvedList);
        result.addAll(endedList);
        return result;
    }

    @Override
    public void fixData(String oldAddress, String newAddress) {
        logger.info("===== Start fix dcc marketing data, old address:{} to new address:{}", oldAddress, newAddress);
        transactionTemplate.execute(transactionStatus -> {
           Integer candy = candyRepository.updateCandy(newAddress, oldAddress);
           logger.info("===== Update candy data, {} -> {}, num:{}", oldAddress, newAddress, candy);

            Integer mrr = miningRewardRecordRepository.updateMiningRewardRecord(newAddress, oldAddress);
            logger.info("===== Update mining reward record data, {} -> {}, num:{}", oldAddress, newAddress, mrr);

            Integer mrri = miningRewardRoundItemRepository.updateMiningRewardRoundItem(newAddress, oldAddress);
            logger.info("===== Update reward round item data, {} -> {}, num:{}", oldAddress, newAddress, mrri);

            Integer redeemToken = redeemTokenRepository.updateRedeemToken(newAddress, oldAddress);
            logger.info("===== Update redeem token data, {} -> {}, num:{}", oldAddress, newAddress, redeemToken);

            Integer rewardLog = rewardLogRepository.updateRewardLog(newAddress, oldAddress);
            logger.info("===== Update reward log data, {} -> {}, num:{}", oldAddress, newAddress, rewardLog);

            return null;
        });
    }
}
