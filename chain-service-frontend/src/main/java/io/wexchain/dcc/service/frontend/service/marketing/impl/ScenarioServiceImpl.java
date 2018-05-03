package io.wexchain.dcc.service.frontend.service.marketing.impl;

import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.model.IdRestriction;
import io.wexchain.dcc.marketing.api.model.RedeemToken;
import io.wexchain.dcc.marketing.api.model.Scenario;
import io.wexchain.dcc.marketing.api.model.request.QueryIdRestrictionRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRedeemTokenRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryScenarioRequest;
import io.wexchain.dcc.service.frontend.common.enums.RedeemTokenQualification;
import io.wexchain.dcc.service.frontend.integration.marketing.RedeemTokenOperationClient;
import io.wexchain.dcc.service.frontend.integration.marketing.ScenarioOperationClient;
import io.wexchain.dcc.service.frontend.model.request.QueryRedeemTokenQualificationRequest;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenQualificationVo;
import io.wexchain.dcc.service.frontend.service.marketing.ScenarioService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ScenarioServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class ScenarioServiceImpl implements ScenarioService {

    @Resource
    private ScenarioOperationClient scenarioOperationClient;

    @Resource
    private RedeemTokenOperationClient redeemTokenOperationClient;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private IdHashServiceImpl idHashService;

    private Map<String, String> map = new HashMap<>();

    {
        map.put("10001001", "id");
        map.put("10001002", "bankCard");
        map.put("10001003", "communicationLog");
    }

    @Override
    public List<RedeemTokenQualificationVo> queryRedeemTokenQualification(
            QueryRedeemTokenQualificationRequest request) {

        List<RedeemTokenQualificationVo> voList = new ArrayList<>();

        QueryScenarioRequest scenarioRequest = new QueryScenarioRequest();
        scenarioRequest.setActivityCode(request.getActivityCode());
        ListResultResponse<Scenario> scenarioList = scenarioOperationClient.queryScenario(scenarioRequest);

        if (CollectionUtils.isNotEmpty(scenarioList.getResultList())) {

            List<String> scenarioCodeList = scenarioList.getResultList().
                    stream().map(Scenario::getCode).collect(Collectors.toList());

                /*QueryRedeemTokenRequest redeemTokenRequest = new QueryRedeemTokenRequest();
                redeemTokenRequest.setAddress(request.getAddress());
                redeemTokenRequest.setScenarioCodeList(scenarioCodeList);
                ListResultResponse<RedeemToken> redeemTokenList =
                        redeemTokenOperationClient.queryRedeemToken(redeemTokenRequest);*/

            String idHash = idHashService.getIdHashByAddress(request.getAddress());
            List<IdRestriction> idRestrictionList = null;
            if (StringUtils.isNotEmpty(idHash)) {
                QueryIdRestrictionRequest idRestrictionRequest = new QueryIdRestrictionRequest();
                idRestrictionRequest.setScenarioCodeList(scenarioCodeList);
                idRestrictionRequest.setIdHash(idHash);
                idRestrictionList = redeemTokenOperationClient.queryIdRestriction(idRestrictionRequest).getResultList();
            }

            for (Scenario scenario : scenarioList.getResultList()) {

                RedeemTokenQualificationVo vo = new RedeemTokenQualificationVo();
                vo.setActivityCode(scenario.getActivityCode());
                vo.setCode(scenario.getCode());
                vo.setName(scenario.getName());
                vo.setAmount(scenario.getAmount());
                vo.setCreatedTime(scenario.getCreatedTime());

                // 是否认证通过
                if (idHashService.getVerifyStatus(map.get(scenario.getCode()), request.getAddress())) {
                    if (CollectionUtils.isNotEmpty(idRestrictionList)) {
                        if (idRestrictionList.stream()
                                .anyMatch(idRestriction -> idRestriction.getScenarioCode().equals(scenario.getCode()))) {
                            vo.setQualification(RedeemTokenQualification.REDEEMED);
                        } else {
                            vo.setQualification(RedeemTokenQualification.AVAILABLE);
                        }
                    } else {
                        vo.setQualification(RedeemTokenQualification.AVAILABLE);
                    }
                } else {
                    vo.setQualification(null);
                }
                voList.add(vo);
            }
        }
        return voList;
    }

}
