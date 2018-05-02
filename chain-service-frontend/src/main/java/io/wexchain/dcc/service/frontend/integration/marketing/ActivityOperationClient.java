package io.wexchain.dcc.service.frontend.integration.marketing;

import com.wexmarket.topia.commons.rpc.ListResultResponse;
import io.wexchain.dcc.marketing.api.model.Activity;
import io.wexchain.dcc.marketing.api.model.request.QueryActivityRequest;

public interface ActivityOperationClient {

    ListResultResponse<Activity> queryActivity(QueryActivityRequest request);

}
