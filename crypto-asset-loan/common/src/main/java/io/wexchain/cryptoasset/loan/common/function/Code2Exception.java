package io.wexchain.cryptoasset.loan.common.function;

import java.util.List;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.BusinessCode;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexmarket.topia.commons.rpc.SystemCode;
import io.wexchain.cryptoasset.loan.common.exception.RpcException;

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
