package io.wexchain.dcc.service.frontend.integration.marketing;

import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.model.Scenario;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;

public interface ScenarioOperationClient {

    ListResultResponse<Scenario> queryScenario(QueryScenarioRequest request);

}
