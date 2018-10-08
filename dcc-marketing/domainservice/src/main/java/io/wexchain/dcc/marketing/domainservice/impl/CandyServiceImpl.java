package io.wexchain.dcc.marketing.domainservice.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.replenishment.ReplenishmentRange;

import io.wexchain.dcc.marketing.api.constant.CandyStatus;
import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.facade.CandyBoxIndex;
import io.wexchain.dcc.marketing.api.facade.PickCandyRequest;
import io.wexchain.dcc.marketing.domain.Candy;
import io.wexchain.dcc.marketing.domainservice.CandyService;
import io.wexchain.dcc.marketing.domainservice.Patroller;
import io.wexchain.dcc.marketing.domainservice.processor.candy.CandyInstruction;
import io.wexchain.dcc.marketing.repository.CandyRepository;

@Service("candyService")
public class CandyServiceImpl implements CandyService, Patroller {

	private static final Logger logger = LoggerFactory.getLogger(CandyServiceImpl.class);

	@Autowired
	private CandyRepository candyRepository;

	@Resource(name = "candyExecutor")
	private OrderExecutor<Candy, CandyInstruction> candyExecutor;

	@Override
	public List<Candy> queryCandyList(CandyBoxIndex index) {
		return candyRepository.findByOwnerAndBoxCodeOrderByIdAsc(index.getOwner(), index.getBoxCode());
	}

	@Override
	public Candy pickCandy(PickCandyRequest request) {
		Optional<Candy> optional = candyRepository.findById(request.getCandyId());
		ErrorCodeValidate.isTrue(optional.isPresent(), MarketingErrorCode.CANDY_NOT_FOUND);
		ErrorCodeValidate.isTrue(optional.get().getOwner().equalsIgnoreCase(request.getOwner()),
				MarketingErrorCode.OWNER_MISMATCH);
		return candyExecutor.execute(optional.get(), CandyInstruction.PICK, null).getModel();
	}

	@Override
	public void patrol() {
		Range<Date> range = new ReplenishmentRange().getRange();
		List<Candy> list = candyRepository.findTop1000ByLastUpdatedTimeBetweenAndStatusOrderByIdAsc(range.getMinimum(),
				range.getMaximum(), CandyStatus.PICKED);
		logger.info("patrol candy size :{}", list.size());
		for (Candy candy : list) {
			candyExecutor.executeAsync(candy, null, null);
		}
	}

}
