package io.wexchain.dcc.service.frontend.ctrlr;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.model.request.AuthenticationRequest;
import io.wexchain.dcc.service.frontend.helper.SignValidator;
import io.wexchain.dcc.service.frontend.model.request.SignBaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * AuthenticationController
 *
 * @author zhengpeng
 */
@RestController(value = "authenticationController")
public class AuthenticationController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SignValidator signValidator;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/secure/ping")
    public String securePing() {
        return "pong";
    }

    @GetMapping("/auth/getNonce")
    public ResultResponse<String> getNonce() {
        String nonce = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(nonce ,"", 5, TimeUnit.MINUTES);
        return ResultResponseUtils.successResultResponse(nonce);
    }

    @PostMapping("/auth")
    public BaseResponse auth(AuthenticationRequest authenticationRequest, HttpServletRequest request) {
        validate(authenticationRequest);
        request.getSession().setAttribute("address", authenticationRequest.getAddress());
        return BaseResponseUtils.successBaseResponse();
    }

    public void validate(AuthenticationRequest authenticationRequest){
        validateNonce(authenticationRequest.getNonce());
        validateSign(authenticationRequest);
    }

    public void validateNonce(String nonce){
       /* ErrorCodeValidate.isTrue(redisTemplate.hasKey(nonce),
                FrontendErrorCode.INVALID_NONCE);
        redisTemplate.delete(nonce);*/
    }
    public void validateSign(SignBaseRequest signBaseRequest){
        /*ErrorCodeValidate.isTrue(
                signValidator.validateSign(signBaseRequest),
                FrontendErrorCode.ILLEGAL_SIGN);*/
    }
}
