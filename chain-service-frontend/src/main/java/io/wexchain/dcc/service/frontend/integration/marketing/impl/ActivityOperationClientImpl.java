package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.facade.ActivityFacade;
import io.wexchain.dcc.marketing.api.model.Activity;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.ActivityOperationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ActivityOperationClientImpl implements ActivityOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "activityFacade")
    private IntegrationProxy<ActivityFacade> activityFacade;

    @Override
    public ListResultResponse<Activity> queryActivity(QueryActivityRequest request) {
        return ExecuteTemplate.execute(() -> activityFacade.buildInst().queryActivity(request), logger, "查询活动", request);
    }
}
