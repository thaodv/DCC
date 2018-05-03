package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.facade.ScenarioFacade;
import io.wexchain.dcc.marketing.api.model.Scenario;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.ext.service.helper.ScenarioResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * ScenarioFacadeImpl
 *
 * @author zhengpeng
 */
@Component("scenarioFacade")
@Validated
public class ScenarioFacadeImpl implements ScenarioFacade {

    @Autowired
    private ScenarioResponseHelper scenarioResponseHelper;

    @Autowired
    private ScenarioService scenarioService;

    @Override
    public ListResultResponse<Scenario> queryScenario(@NotNull @Valid QueryScenarioRequest request) {
        try {
            return scenarioResponseHelper.returnListSuccess(scenarioService.queryScenario(request));
        } catch (Exception e) {
            return ListResultResponseUtils.exceptionListResultResponse(e);
        }
    }
}
