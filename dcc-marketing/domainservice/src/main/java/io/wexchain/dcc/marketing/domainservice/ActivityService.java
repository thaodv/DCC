package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.domain.Activity;

import java.util.List;

public interface ActivityService {

    List<Activity> queryActivity(QueryActivityRequest request);

}
