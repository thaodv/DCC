package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.facade.ScenarioFacade;
import io.wexchain.dcc.marketing.api.model.Scenario;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.ScenarioOperationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScenarioOperationClientImpl implements ScenarioOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "scenarioFacade")
    private IntegrationProxy<ScenarioFacade> scenarioFacade;


    @Override
    public ListResultResponse<Scenario> queryScenario(QueryScenarioRequest request) {
        return ExecuteTemplate.execute(() ->
                scenarioFacade.buildInst().queryScenario(request), logger, "查询活动场景", request);
    }
}
