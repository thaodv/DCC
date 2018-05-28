package io.wexchain.cryptoasset.loan.service.function.wexyun.util;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexyun.open.api.response.BaseResponse;

public class WexyunCode2Exception {

	public static void handleResponse(BaseResponse baseResponse) {
		if (baseResponse.isSuccess()) {
			throw new ErrorCodeException(baseResponse.getResponseCode(), baseResponse.getResponseMessage());
		}
	}
}
