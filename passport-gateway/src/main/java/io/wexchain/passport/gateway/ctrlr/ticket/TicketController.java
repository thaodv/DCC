package io.wexchain.passport.gateway.ctrlr.ticket;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.passport.gateway.ctrlr.captcha.CaptchaAdapter;
import io.wexchain.passport.gateway.ctrlr.captcha.CaptchaQuestion;
import io.wexchain.passport.gateway.ctrlr.commons.ChallengeForm;
import io.wexchain.passport.gateway.service.risk.AccessRestriction;
import io.wexchain.passport.gateway.service.risk.RiskMeasurer;

import javax.annotation.Resource;

@RequestMapping("/ticket/1")
@RestController
public class TicketController {

	public static final int DEFAULT_EXPIRED_SECOND = 60;

	public static final String DEFAULT_KEY_TEMPLATE = "passportCaptcha:%s";

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Resource(name="grantedRiskMeasurer")
	private RiskMeasurer riskMeasurer;

	@Autowired
	private CaptchaAdapter captchaAdapter;

	private int expiredSecond = DEFAULT_EXPIRED_SECOND;

	private String keyTemplate = DEFAULT_KEY_TEMPLATE;

	@Value("${captcha.answer}")
	private boolean answer;

	@RequestMapping(value = "/getTicket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResultResponse<CaptchaQuestion> getTicket(WebRequest webRequest) throws IOException {
		CaptchaQuestion captchaQuestion = new CaptchaQuestion();
		AccessRestriction accessRestriction = riskMeasurer.risk(webRequest);
		captchaQuestion.setAccessRestriction(accessRestriction);

		if (accessRestriction == AccessRestriction.REJECTED) {
			return ResultResponseUtils.successResultResponse(captchaQuestion);
		}
		String ticket = UUID.randomUUID().toString();
		captchaQuestion.setTicket(ticket);
		String key = formatKey(ticket);
		String code = null;
		if(accessRestriction == AccessRestriction.GRANTED){
			code = AccessRestriction.GRANTED.name();
		}else {
			Pair<String, byte[]> createImg2 = captchaAdapter.createImg();
			if (answer) {
				captchaQuestion.setAnswer(createImg2.getLeft());
			}
			captchaQuestion.setImage(createImg2.getRight());
			code = createImg2.getLeft();
		}
		redisTemplate.opsForValue().set(key, code, expiredSecond, TimeUnit.SECONDS);
		captchaQuestion.setExpiredSeconds(expiredSecond);
		return ResultResponseUtils.successResultResponse(captchaQuestion);
	}

	public void verifyChallenge(ChallengeForm challengeForm) {
		String ticket = challengeForm.getTicket();
		String formatKey = formatKey(ticket);
		String code = redisTemplate.opsForValue().get(formatKey);
		if (code != null) {
			redisTemplate.delete(formatKey);
		} else {
			ErrorCodeValidate.fail(TicketErrorCode.TICKET_INVALID);
		}
		ErrorCodeValidate.isTrue(code.equals(AccessRestriction.GRANTED.name()) || code.equalsIgnoreCase(challengeForm.getCode()), TicketErrorCode.CHALLENGE_FAILURE,
				challengeForm.getCode(), answer ? code : "");
	}

	private String formatKey(String ticket) {
		return String.format(keyTemplate, ticket);
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