package io.wexchain.dcc.marketing.domainservice.impl;

import io.wexchain.dcc.marketing.api.constant.ActivityStatus;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.repository.ActivityRepository;
import io.wexchain.dcc.marketing.repository.query.ActivityQueryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ActivityServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

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
}
