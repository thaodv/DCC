package io.wexchain.dcc.service.frontend.ctrlr;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;
import io.wexchain.dcc.service.frontend.service.wexyun.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * AuthenticationController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/member")
public class LoginController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationController authenticationController;

    @PostMapping("/register")
    public BaseResponse register(@Valid RegisterRequest registerRequest) {
        authenticationController.validate(registerRequest);
        String register = memberService.register(registerRequest);
        return BaseResponseUtils.successBaseResponse(register);
    }

    @GetMapping("/login")
    public BaseResponse login(@RequestParam(value = "validNonceError", required = false) String validNonceError,
                              @RequestParam(value = "validSignError", required = false) String validSignError) {
        ErrorCodeValidate.isTrue(validNonceError==null,FrontendErrorCode.INVALID_NONCE);
        ErrorCodeValidate.isTrue(validSignError==null,FrontendErrorCode.ILLEGAL_SIGN);
        return BaseResponseUtils.successBaseResponse("登录成功");
    }

}
