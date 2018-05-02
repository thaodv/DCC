package io.wexchain.dcc.service.frontend.service.marketing.impl;

import io.wexchain.dcc.marketing.api.model.Activity;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.service.frontend.common.constants.FrontendWebConstants;
import io.wexchain.dcc.service.frontend.integration.marketing.ActivityOperationClient;
import io.wexchain.dcc.service.frontend.model.request.QueryActivityVoRequest;
import io.wexchain.dcc.service.frontend.model.vo.ActivityVo;
import io.wexchain.dcc.service.frontend.service.marketing.ActivityService;
import io.wexchain.dcc.service.frontend.utils.ResultResponseValidator;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService, FrontendWebConstants {

    @Resource
    private ActivityOperationClient activityOperationClient;

    @Autowired
    private MapperFacade mapperFacade;

    @Override
    public List<ActivityVo> queryActivity(QueryActivityVoRequest request) {
        QueryActivityRequest queryActivityRequest = new QueryActivityRequest();
        queryActivityRequest.setMerchantCode(request.getMerchantCode());
        List<Activity> list = ResultResponseValidator.getListResult(
                activityOperationClient.queryActivity(queryActivityRequest));
        return mapperFacade.mapAsList(list, ActivityVo.class);
    }
}
