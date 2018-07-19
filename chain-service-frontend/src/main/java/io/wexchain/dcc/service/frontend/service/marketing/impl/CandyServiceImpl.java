package io.wexchain.dcc.service.frontend.service.marketing.impl;

import io.wexchain.dcc.marketing.api.facade.CandyBoxIndex;
import io.wexchain.dcc.marketing.api.facade.PickCandyRequest;
import io.wexchain.dcc.marketing.api.model.candy.Candy;
import io.wexchain.dcc.service.frontend.common.convertor.CandyConvertor;
import io.wexchain.dcc.service.frontend.integration.marketing.CandyOperationClient;
import io.wexchain.dcc.service.frontend.model.vo.CandyVo;
import io.wexchain.dcc.service.frontend.service.marketing.CandyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EcoServiceImpl
 *
 * @author zhengpeng
 */
@Service(value = "candyServiceImpl")
public class CandyServiceImpl implements CandyService {

    private Logger logger = LoggerFactory.getLogger(CandyServiceImpl.class);

    @Autowired
    private CandyOperationClient candyOperationClient;

    @Override
    public List<CandyVo> queryCandyList(String address, String boxCode) {

        List<Candy> candyList = candyOperationClient.queryCandyList(new CandyBoxIndex(address, boxCode));
        return CandyConvertor.convert(candyList);
    }

    @Override
    public CandyVo pickCandy(String address, Long candyId) {
        PickCandyRequest pickCandyRequest = new PickCandyRequest();
        pickCandyRequest.setCandyId(candyId);
        pickCandyRequest.setOwner(address);
        Candy candy = candyOperationClient.pickCandy(pickCandyRequest);
        return CandyConvertor.convert(candy);
    }
}
