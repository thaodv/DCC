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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

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

    private static final int PAGE_SIZE = 200;

    @Override
    public Optional<MiningRewardRound> getMiningRewardRoundNullable(Date roundTime) {
        return Optional.ofNullable(miningRewardRoundRepository.findByRoundTime(roundTime));
    }

    @Override
    public MiningRewardRound createMiningRewardRound(Date roundTime) {

        Activity activity = activityService.getActivityByCode(miningActivityCode);

        MiningRewardRound miningRewardRound = getMiningRewardRoundNullable(roundTime)
                .orElseGet(() -> transactionTemplate.execute(transactionStatus -> {

            long totalCount = lastLoginTimeRepository.count();
            if (totalCount == 0) {
                MiningRewardRound newRound = new MiningRewardRound();
                newRound.setRoundTime(roundTime);
                newRound.setStatus(MiningRewardRoundStatus.SKIPPED);
                newRound.setActivity(activity);
                return  miningRewardRoundRepository.save(newRound);
            }

            MiningRewardRound newRound = new MiningRewardRound();
            newRound.setRoundTime(roundTime);
            newRound.setStatus(MiningRewardRoundStatus.CREATED);
            newRound.setActivity(activity);
            newRound =  miningRewardRoundRepository.save(newRound);

            long totalPage = (totalCount + PAGE_SIZE - 1) / PAGE_SIZE;
            for (int page = 0; page <= totalPage; page++) {
                Page<LastLoginTime> pageResult = lastLoginTimeRepository.findAll(PageRequest.of(page, PAGE_SIZE));
                List<LastLoginTime> loginList = pageResult.getContent();
                List<MiningRewardRoundItem> itemList = new ArrayList<>(loginList.size());
                for (LastLoginTime lastLoginTime : loginList) {
                    MiningRewardRoundItem item = new MiningRewardRoundItem();
                    item.setAddress(lastLoginTime.getAddress());
                    item.setStatus(MiningRewardRoundItemStatus.CREATED);
                    item.setMiningRewardRound(newRound);
                    itemList.add(item);
                }
                miningRewardRoundItemRepository.saveAll(itemList);
            }
            return newRound;
        }));

        miningRewardRoundExecutor.executeAsync(miningRewardRound, null, null);
        return miningRewardRound;
    }
}
