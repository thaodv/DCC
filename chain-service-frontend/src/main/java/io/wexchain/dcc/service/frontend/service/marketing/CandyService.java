package io.wexchain.dcc.service.frontend.service.marketing;

import io.wexchain.dcc.service.frontend.model.vo.CandyVo;

import java.util.List;


public interface CandyService {

    List<CandyVo> queryCandyList(String address, String activityCode);

    CandyVo pickCandy(String address, Long candyId);

}
