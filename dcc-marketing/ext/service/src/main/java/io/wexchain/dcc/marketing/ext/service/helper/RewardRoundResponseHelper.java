package io.wexchain.dcc.marketing.ext.service.helper;

import com.wexmarket.topia.commons.data.rpc.ResponseHelper;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.RewardRound;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RewardRoundResponseHelper extends ResponseHelper<RewardRound, io.wexchain.dcc.marketing.api.model.RewardRound> {

	{
		setModelClass(io.wexchain.dcc.marketing.api.model.RewardRound.class);
	}

	@Autowired
	@Qualifier("dozerBeanMapper")
	@Override
	public void setMapper(Mapper mapper) {
		super.setMapper(mapper);
	}

}
