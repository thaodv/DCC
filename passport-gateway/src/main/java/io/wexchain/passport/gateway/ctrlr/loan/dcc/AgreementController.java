package io.wexchain.passport.gateway.ctrlr.loan.dcc;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.data.rpc.PageTransformer;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.juzix.contract.loan.dcc.Agreement;
import io.wexchain.passport.gateway.service.loan.dcc.AgreementServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;

@RestController
@RequestMapping("/dcc/loan/agreement/1")
public class AgreementController {

	@Autowired
	protected AgreementServiceProxy agreementServiceProxy;

	@RequestMapping(value = "/getAbi", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getAbi() throws IOException {
		return ResultResponseUtils.successResultResponse(agreementServiceProxy.getAbi());
	}

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress() throws IOException {
		return ResultResponseUtils.successResultResponse(agreementServiceProxy.getContractAddress());
	}

	@RequestMapping(value = "/getAgreement", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<Agreement> getAgreement(@RequestParam @NotBlank Long agreementId) throws IOException {
		Agreement agreement = agreementServiceProxy.getAgreement(agreementId);
		return ResultResponseUtils.successResultResponse(agreement);
	}

	@RequestMapping(value = "/getAgreementArrayLengthByIdHashIndex", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<BigInteger> getAgreementArrayLengthByIdHashIndex(@NotNull byte[] idHash) throws IOException {
		BigInteger length = agreementServiceProxy.getAgreementArrayLengthByIdHashIndex(idHash);
		return ResultResponseUtils.successResultResponse(length);
	}

	@RequestMapping(value = "/queryAgreementPageByIdHashIndex", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<Pagination<Agreement>> queryAgreementPageByIdHashIndex(@NotNull @Valid QueryOrderByIdHash queryOrderParam)
			throws IOException {
		Page<Agreement> queryOrderPageByBorrowIndex = agreementServiceProxy.queryAgreementPageByIdHashIndex(queryOrderParam);
		Pagination<Agreement> transform = PageTransformer.transform(queryOrderPageByBorrowIndex);
		return ResultResponseUtils.successResultResponse(transform);
	}

	@ExceptionHandler(ErrorCodeException.class)
	@ResponseBody
	public BaseResponse conflict(ErrorCodeException e) {
		return BaseResponseUtils.errorCodeExceptionResponse(e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public BaseResponse others(Exception e) {
		return BaseResponseUtils.exceptionBaseResponse(e);
	}

}
