package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.Scenario;

import java.util.List;

public interface ScenarioService {

    Scenario getScenario(String scenarioCode);

    List<Scenario> queryScenario(QueryScenarioRequest request);

}
