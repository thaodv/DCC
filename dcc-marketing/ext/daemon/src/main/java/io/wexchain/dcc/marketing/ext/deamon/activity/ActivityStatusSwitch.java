package io.wexchain.dcc.marketing.ext.deamon.activity;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.competition.LockTemplate;
import com.wexmarket.topia.commons.data.page.PageUtils;
import com.wexmarket.topia.commons.pagination.PageParam;
import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domainservice.processor.activity.ActivityInstruction;
import io.wexchain.dcc.marketing.repository.ActivityRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

/**
 * 自动切换活动状态
 *
 * @author zhengpeng
 */
@Component
public class ActivityStatusSwitch {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private LockTemplate lockTemplate;

    @Resource(name = "activityExecutor")
    private OrderExecutor<Activity, ActivityInstruction> activityExecutor;

    private static final int PAGE_SIZE = 20;

    @Scheduled(cron = "${patrol.cron.activity}")
    public void patrol() {

        lockTemplate.execute("ActivityPatroller", null, () -> {
            logger.debug("patrol started ");

            DateTime now = new DateTime();
            Date beginTime = now.minusDays(365).toDate();
            Date endTime = now.minusMinutes(1).toDate();

            int count = activityRepository.countByCreatedTimeBetweenAndStatusIn(
                    beginTime, endTime,
                    Arrays.asList(ActivityStatus.SHELVED, ActivityStatus.STARTED));
            int totalPage = (count + PAGE_SIZE - 1) / PAGE_SIZE;

            for (int i = totalPage; i > 0; i--) {
                PageRequest pageRequest = PageUtils.convert(new PageParam(i - 1, PAGE_SIZE));
                Page<Activity> activities = activityRepository.findByCreatedTimeBetweenAndStatusIn(
                        beginTime, endTime,
                        Arrays.asList(ActivityStatus.SHELVED, ActivityStatus.STARTED),
                        pageRequest);
                if (CollectionUtils.isNotEmpty(activities.getContent())) {
                    activities.getContent().forEach(act -> {
                        try {
                            activityExecutor.execute(act, null, null);
                        } catch (Exception e) {
                            logger.error("Advance activity fail, act code:{}", act.getCode(), e);
                        }
                    });
                }
            }
            return null;
        });
    }

}
