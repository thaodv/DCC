package io.wexchain.dcc.marketing.ext.service.helper;

import com.wexmarket.topia.commons.data.rpc.ResponseHelper;
import io.wexchain.dcc.marketing.domain.RedeemToken;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RedeemTokenResponseHelper extends ResponseHelper<RedeemToken, io.wexchain.dcc.marketing.api.model.RedeemToken> {

	{
		setModelClass(io.wexchain.dcc.marketing.api.model.RedeemToken.class);
	}

	@Autowired
	@Qualifier("dozerBeanMapper")
	@Override
	public void setMapper(Mapper mapper) {
		super.setMapper(mapper);
	}

}
