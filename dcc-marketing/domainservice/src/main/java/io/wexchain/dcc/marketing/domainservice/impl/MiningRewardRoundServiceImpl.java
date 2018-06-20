package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import io.wexchain.dcc.marketing.api.constant.MiningRewardRoundStatus;
import io.wexchain.dcc.marketing.common.constant.MiningRewardRoundItemStatus;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.LastLoginTime;
import io.wexchain.dcc.marketing.domain.MiningRewardRound;
import io.wexchain.dcc.marketing.domain.MiningRewardRoundItem;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRoundService;
import io.wexchain.dcc.marketing.domainservice.processor.order.mining.rewardround.MiningRewardRoundInstruction;
import io.wexchain.dcc.marketing.repository.LastLoginTimeRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRoundItemRepository;
import io.wexchain.dcc.marketing.repository.MiningRewardRoundRepository;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * MiningRewardRoundServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class MiningRewardRoundServiceImpl implements MiningRewardRoundService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private LastLoginTimeRepository lastLoginTimeRepository;

    @Autowired
    private MiningRewardRoundRepository miningRewardRoundRepository;

    @Autowired
    private MiningRewardRoundItemRepository miningRewardRoundItemRepository;

    @Autowired
    private ActivityService activityService;

    @Resource(name = "miningRewardRoundExecutor")
    private OrderExecutor<MiningRewardRound, MiningRewardRoundInstruction> miningRewardRoundExecutor;

    private String miningActivityCode = "10004";

    @Override
    public Optional<MiningRewardRound> getMiningRewardRoundNullable(Date roundTime) {
        return Optional.ofNullable(miningRewardRoundRepository.findByRoundTime(roundTime));
    }

    @Override
    public MiningRewardRound createMiningRewardRound(Date roundTime) {

        Activity activity = activityService.getActivityByCode(miningActivityCode);

        MiningRewardRound miningRewardRound = getMiningRewardRoundNullable(roundTime)
                .orElseGet(() -> transactionTemplate.execute(transactionStatus -> {
            MiningRewardRound newRound = new MiningRewardRound();
            newRound.setRoundTime(roundTime);
            newRound.setStatus(MiningRewardRoundStatus.CREATED);
            newRound.setActivity(activity);
            newRound =  miningRewardRoundRepository.save(newRound);

            List<LastLoginTime> loginList =
                    lastLoginTimeRepository.findByLastLoginTimeAfter(DateTime.now().minusHours(48).toDate());
            Validate.notEmpty(loginList, "No user login in latest 48 hours, skip create reward round");

            List<MiningRewardRoundItem> itemList = new ArrayList<>(loginList.size());
            for (LastLoginTime lastLoginTime : loginList) {
                MiningRewardRoundItem item = new MiningRewardRoundItem();
                item.setAddress(lastLoginTime.getAddress());
                item.setStatus(MiningRewardRoundItemStatus.CREATED);
                item.setMiningRewardRound(newRound);
                itemList.add(item);
            }
            miningRewardRoundItemRepository.saveAll(itemList);

            return newRound;
        }));

        miningRewardRoundExecutor.executeAsync(miningRewardRound, null, null);
        return miningRewardRound;
    }
}
