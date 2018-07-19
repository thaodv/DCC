package io.wexchain.dcc.service.frontend.integration.marketing;

import io.wexchain.dcc.marketing.api.facade.CandyBoxIndex;
import io.wexchain.dcc.marketing.api.facade.PickCandyRequest;
import io.wexchain.dcc.marketing.api.model.candy.Candy;

import java.util.List;

public interface CandyOperationClient {

    /**
     * 查询糖果
     *
     * @param index
     * @return
     */
    List<Candy> queryCandyList(CandyBoxIndex index);

    /**
     * 领取糖果
     *
     * @param request
     * @return
     */
    Candy pickCandy(PickCandyRequest request);

}
