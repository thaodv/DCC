package io.wexchain.passport.gateway.ctrlr.ca;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.juzix.contract.passport.KeyDeletedEvent;
import io.wexchain.juzix.contract.passport.KeyPutEvent;
import io.wexchain.passport.gateway.ctrlr.captcha.CaptchaAdapter;
import io.wexchain.passport.gateway.ctrlr.captcha.CaptchaQuestion;
import io.wexchain.passport.gateway.ctrlr.commons.ChallengeForm;
import io.wexchain.passport.gateway.ctrlr.commons.SignMessageRequest;
import io.wexchain.passport.gateway.ctrlr.ticket.TicketErrorCode;
import io.wexchain.passport.gateway.service.ca.CaRepo;
import io.wexchain.passport.gateway.service.contract.ReceiptResult;
import io.wexchain.passport.gateway.service.risk.AccessRestriction;
import io.wexchain.passport.gateway.service.risk.RiskMeasurer;

@RestController
@RequestMapping("/ca/1")
public class CaController {

	private static final Logger logger = LoggerFactory.getLogger(CaController.class);
	public static final int DEFAULT_EXPIRED_SECOND = 60;

	public static final String DEFAULT_KEY_TEMPLATE = "passportCaptcha:%s";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Resource(name = "captchaRiskMeasurer")
	private RiskMeasurer riskMeasurer;

	@Autowired
	private CaptchaAdapter captchaAdapter;

	@Autowired
	private CaRepo caRepo;

	private int expiredSecond = DEFAULT_EXPIRED_SECOND;

	private String keyTemplate = DEFAULT_KEY_TEMPLATE;

	@Value("${captcha.answer}")
	private boolean answer;

	@RequestMapping(value = "/getContractAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<String> getContractAddress() throws IOException {
		return ResultResponseUtils.successResultResponse(caRepo.getContractAddress());
	}

	/**
	 * 使用公共getTicket方法
	 * 
	 * @param webRequest
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/getTicket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Deprecated
	public ResultResponse<CaptchaQuestion> getTicket(WebRequest webRequest) throws IOException {
		CaptchaQuestion captchaQuestion = new CaptchaQuestion();
		AccessRestriction accessRestriction = riskMeasurer.risk(webRequest);
		captchaQuestion.setAccessRestriction(accessRestriction);
		if (accessRestriction == AccessRestriction.REJECTED) {
			return ResultResponseUtils.successResultResponse(captchaQuestion);
		}
		String ticket = UUID.randomUUID().toString();
		captchaQuestion.setTicket(ticket);
		String code = null;
		Pair<String, byte[]> createImg2 = captchaAdapter.createImg();
		if (answer) {
			captchaQuestion.setAnswer(createImg2.getLeft());
		}
		captchaQuestion.setImage(createImg2.getRight());
		code = createImg2.getLeft();
		String key = formatKey(ticket);
		redisTemplate.opsForValue().set(key, code, expiredSecond, TimeUnit.SECONDS);
		captchaQuestion.setExpiredSeconds(expiredSecond);
		return ResultResponseUtils.successResultResponse(captchaQuestion);
	}

	@RequestMapping(value = "/uploadPubKey", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> uploadPubKey(@NotNull @Valid SignMessageRequest signMessageRequest)
			throws IOException {
		verifyChallenge(signMessageRequest);
		String txHash = caRepo.put(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(txHash);
	}

	@RequestMapping(value = "/deletePubKey", method = RequestMethod.POST)
	@ResponseBody
	public ResultResponse<String> deletePubKey(@NotNull @Valid SignMessageRequest signMessageRequest)
			throws IOException {
		verifyChallenge(signMessageRequest);
		String delete = caRepo.delete(signMessageRequest.getSignMessage());
		return ResultResponseUtils.successResultResponse(delete);
	}

	/**
	 * 使用公共hasReceipt
	 * 
	 * @param txHash
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/hasReceipt", method = RequestMethod.POST)
	@ResponseBody
	@Deprecated
	public ResultResponse<Boolean> hasReceipt(@RequestParam @NotBlank String txHash) throws IOException {
		ReceiptResult receiptResult = caRepo.getReceiptResult(txHash, null);
		return ResultResponseUtils.successResultResponse(receiptResult.isHasReceipt());
	}

	@RequestMapping(value = "/getPubKey", method = RequestMethod.GET)
	@ResponseBody
	public ResultResponse<byte[]> getPubKey(@RequestParam @NotBlank String address) throws IOException {
		byte[] bs = caRepo.get(address);
		return ResultResponseUtils.successResultResponse(bs);
	}

	@RequestMapping(value = "/getKeyPutEventByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<KeyPutEvent> getKeyPutEventByTx(@RequestParam @NotBlank String txHash)
			throws IOException {
		List<KeyPutEvent> keyPutEvents = caRepo.getKeyPutEvents(txHash);
		return ListResultResponseUtils.successListResultResponse(keyPutEvents);
	}

	@RequestMapping(value = "/getKeyDeletedEventByTx", method = RequestMethod.GET)
	@ResponseBody
	public ListResultResponse<KeyDeletedEvent> getKeyDeletedEventByTx(@RequestParam @NotBlank String txHash)
			throws IOException {
		List<KeyDeletedEvent> keyPutEvents = caRepo.getKeyDeletedEvents(txHash);
		return ListResultResponseUtils.successListResultResponse(keyPutEvents);
	}

	private void verifyChallenge(ChallengeForm challengeForm) {
		String ticket = challengeForm.getTicket();
		String formatKey = formatKey(ticket);
		String code = redisTemplate.opsForValue().get(formatKey);

		if (code != null) {
			redisTemplate.delete(formatKey);
		} else {
			ErrorCodeValidate.fail(CaErrorCode.TICKET_INVALID);
		}
		ErrorCodeValidate.isTrue(
				code.equals(AccessRestriction.GRANTED.name()) || code.equalsIgnoreCase(challengeForm.getCode()),
				TicketErrorCode.CHALLENGE_FAILURE, challengeForm.getCode(), answer ? code : "");
	}

	private String formatKey(String ticket) {
		return String.format(keyTemplate, ticket);
	}

	@ExceptionHandler(ErrorCodeException.class)
	@ResponseBody
	public BaseResponse conflict(ErrorCodeException e) {
		logger.warn("", e);
		return BaseResponseUtils.errorCodeExceptionResponse(e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public BaseResponse others(Exception e) {
		logger.warn("", e);
		return BaseResponseUtils.exceptionBaseResponse(e);
	}
}