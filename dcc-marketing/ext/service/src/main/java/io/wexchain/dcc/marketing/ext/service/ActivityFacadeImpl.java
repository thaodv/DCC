package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.facade.ActivityFacade;
import io.wexchain.dcc.marketing.api.model.Activity;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.ext.service.helper.ActivityResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ActivityFacadeImpl
 *
 * @author zhengpeng
 */
@Component("activityFacade")
@Validated
public class ActivityFacadeImpl implements ActivityFacade {

    @Autowired
    private ActivityResponseHelper activityResponseHelper;

    @Autowired
    private ActivityService activityService;

    @Override
    public ListResultResponse<Activity> queryActivity(QueryActivityRequest request) {
        try {
            List<io.wexchain.dcc.marketing.domain.Activity> result = activityService.queryActivity(request);
            return activityResponseHelper.returnListSuccess(result);
        } catch (Exception e) {
            return ListResultResponseUtils.exceptionListResultResponse(e);
        }
    }
}
