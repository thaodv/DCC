package io.wexchain.dcc.marketing.domainservice;

import java.util.List;

import io.wexchain.dcc.marketing.api.facade.CandyBoxIndex;
import io.wexchain.dcc.marketing.api.facade.PickCandyRequest;
import io.wexchain.dcc.marketing.domain.Candy;

public interface CandyService {
	List<Candy> queryCandyList(CandyBoxIndex index);

	Candy pickCandy(PickCandyRequest request);

}
