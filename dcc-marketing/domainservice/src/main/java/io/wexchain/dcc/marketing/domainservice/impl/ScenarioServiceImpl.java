package io.wexchain.dcc.marketing.domainservice.impl;

import java.util.List;

import io.wexchain.dcc.marketing.repository.query.ScenarioQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;

import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.model.ScenarioIndex;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;
import io.wexchain.dcc.marketing.domain.Scenario;
import io.wexchain.dcc.marketing.domainservice.ScenarioService;
import io.wexchain.dcc.marketing.repository.ScenarioRepository;

/**
 * ScenarioServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ScenarioServiceImpl implements ScenarioService {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Override
    public Scenario getScenarioById(Long id) {
        return ErrorCodeValidate.notNull(
                scenarioRepository.findById(id).orElse(null),
                MarketingErrorCode.SCENARIO_NOT_FOUND);
    }

    @Override
    public Scenario getScenarioByCode(String activityCode, String scenarioCode) {
        return ErrorCodeValidate.notNull(
                scenarioRepository.findByActivityCodeAndCode(activityCode, scenarioCode),
                MarketingErrorCode.SCENARIO_NOT_FOUND);
    }

    @Override
    public Scenario getScenarioByIndex(ScenarioIndex scenarioIndex) {
        return ErrorCodeValidate.notNull(
                scenarioRepository.findByActivityMerchantCodeAndActivityCodeAndCode(
                        scenarioIndex.getMerchantCode(),
                        scenarioIndex.getActivityCode(),
                        scenarioIndex.getCode()),
                MarketingErrorCode.SCENARIO_NOT_FOUND);
    }

    @Override
    public Scenario getScenarioByCode(String scenarioCode) {
        return ErrorCodeValidate.notNull(
                scenarioRepository.findByCode(scenarioCode), MarketingErrorCode.SCENARIO_NOT_FOUND);
    }

    @Override
    public List<Scenario> queryScenario(QueryScenarioRequest request) {
        return scenarioRepository.findAll(ScenarioQueryBuilder.query(request));
    }


}
