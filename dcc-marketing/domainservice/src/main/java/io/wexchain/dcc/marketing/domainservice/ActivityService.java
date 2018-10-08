package io.wexchain.dcc.marketing.domainservice;

import java.util.List;

import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.domain.Activity;

public interface ActivityService {

    Activity getActivityById(Long id);

    Activity getActivityByCode(String code);

    List<Activity> queryActivity(QueryActivityRequest request);

    void fixData(String oldAddress, String newAddress);
}
