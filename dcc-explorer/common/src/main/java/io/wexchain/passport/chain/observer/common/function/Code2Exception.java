package io.wexchain.passport.chain.observer.common.function;

import io.wexchain.passport.chain.observer.common.exception.RpcException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.rpc.*;

import java.util.List;

public class Code2Exception {

	public static void handleResponse(BaseResponse baseResponse) {
		if (!SystemCode.SUCCESS.equals(baseResponse.getSystemCode())) {
			throw new RpcException(baseResponse.getMessage());
		}
		if (!BusinessCode.SUCCESS.name().equals(baseResponse.getBusinessCode())) {
			throw new ErrorCodeException(baseResponse.getBusinessCode(), baseResponse.getMessage());
		}
	}

	public static <R> R handleResultResponse(ResultResponse<R> resultResponse) {
		handleResponse(resultResponse);
		return resultResponse.getResult();
	}

	public static <R> List<R> handleListResultResponse(ListResultResponse<R> listResultResponse) {
		handleResponse(listResultResponse);
		return listResultResponse.getResultList();
	}
}
