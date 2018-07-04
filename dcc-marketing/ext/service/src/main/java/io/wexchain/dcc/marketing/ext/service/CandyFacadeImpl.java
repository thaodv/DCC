package io.wexchain.dcc.marketing.ext.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.dcc.marketing.api.facade.CandyBoxIndex;
import io.wexchain.dcc.marketing.api.facade.CandyFacade;
import io.wexchain.dcc.marketing.api.facade.PickCandyRequest;
import io.wexchain.dcc.marketing.api.model.candy.Candy;
import io.wexchain.dcc.marketing.domainservice.CandyService;
import io.wexchain.dcc.marketing.ext.service.helper.CandyResponseHelper;

@Component("candyFacade")
@Validated
public class CandyFacadeImpl implements CandyFacade {
	@Autowired
	private CandyResponseHelper candyResponseHelper;

	@Autowired
	private CandyService candyService;

	@Override
	public ListResultResponse<Candy> queryCandyList(CandyBoxIndex index) {
		try {
			List<io.wexchain.dcc.marketing.domain.Candy> queryCandyList = candyService.queryCandyList(index);
			return candyResponseHelper.returnListSuccess(queryCandyList);
		} catch (Exception e) {
			return ListResultResponseUtils.exceptionListResultResponse(e);
		}
	}

	@Override
	public ResultResponse<Candy> pickCandy(PickCandyRequest request) {
		try {
			io.wexchain.dcc.marketing.domain.Candy pickCandy = candyService.pickCandy(request);
			return candyResponseHelper.returnSuccess(pickCandy);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

}
