package io.wexchain.dcc.service.frontend.integration.marketing.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.marketing.api.facade.CandyBoxIndex;
import io.wexchain.dcc.marketing.api.facade.CandyFacade;
import io.wexchain.dcc.marketing.api.facade.PickCandyRequest;
import io.wexchain.dcc.marketing.api.model.candy.Candy;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.marketing.CandyOperationClient;
import io.wexchain.dcc.service.frontend.utils.ResultResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CandyOperationClientImpl implements CandyOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "candyFacade")
    private IntegrationProxy<CandyFacade> candyFacade;

    @Override
    public List<Candy> queryCandyList(CandyBoxIndex index) {
        ListResultResponse<Candy> resultResponse = ExecuteTemplate.execute(() ->
                candyFacade.buildInst().queryCandyList(index), logger, "查询糖果", index);
        return ResultResponseValidator.getListResult(resultResponse);
    }

    @Override
    public Candy pickCandy(PickCandyRequest request) {
        ResultResponse<Candy> resultResponse = ExecuteTemplate.execute(() ->
                candyFacade.buildInst().pickCandy(request), logger, "领取糖果", request);
        return ResultResponseValidator.getResult(resultResponse);
    }
}
