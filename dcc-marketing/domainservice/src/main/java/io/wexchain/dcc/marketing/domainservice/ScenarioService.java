package io.wexchain.dcc.marketing.domainservice;

import java.util.List;

import io.wexchain.dcc.marketing.api.model.ScenarioIndex;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;
import io.wexchain.dcc.marketing.domain.Scenario;

public interface ScenarioService {

    Scenario getScenarioById(Long id);

    Scenario getScenarioByCode(String activityCode, String scenarioCode);

    Scenario getScenarioByCode(String scenarioCode);

    List<Scenario> queryScenario(QueryScenarioRequest request);

    Scenario getScenarioByIndex(ScenarioIndex scenarioIndex);
}
