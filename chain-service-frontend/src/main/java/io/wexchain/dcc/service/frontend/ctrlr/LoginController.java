package io.wexchain.dcc.service.frontend.ctrlr;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.service.frontend.common.convertor.MemberConvertor;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.model.request.AuthenticationRequest;
import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;
import io.wexchain.dcc.service.frontend.model.vo.MemberVo;
import io.wexchain.dcc.service.frontend.service.wexyun.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * AuthenticationController
 *
 * @author zhengpeng
 */
@RestController
public class LoginController extends SecurityBaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationController authenticationController;

    @PostMapping("/member/register")
    public ResultResponse register(@Valid RegisterRequest registerRequest, HttpServletRequest request) {
        authenticationController.validate(registerRequest);
        String register = memberService.registerAndLogin(registerRequest,request);
        return ResultResponseUtils.successResultResponse(register);
    }

    @PostMapping("/member/getByIdentity")
    public ResultResponse<MemberVo> register(AuthenticationRequest authenticationRequest) {
        authenticationController.validate(authenticationRequest);
        Member member = memberService.getByIdentity(authenticationRequest.getAddress());
        return ResultResponseUtils.successResultResponse(MemberConvertor.convert(member,false));
    }

    @GetMapping("/member/login")
    public BaseResponse login(@RequestParam(value = "validNonceError", required = false) String validNonceError,
                              @RequestParam(value = "validSignError", required = false) String validSignError) {
        ErrorCodeValidate.isTrue(validNonceError==null,FrontendErrorCode.INVALID_NONCE);
        ErrorCodeValidate.isTrue(validSignError==null,FrontendErrorCode.ILLEGAL_SIGN);
        return BaseResponseUtils.successBaseResponse("登录成功");
    }

    @PostMapping("/secure/member/queryByInvited")
    public ListResultResponse<MemberVo> queryByInvited() {
        List<Member> members = memberService.queryByInvited(getMemberId().toString());
        return ListResultResponseUtils.successListResultResponse(MemberConvertor.convert(members));
    }

}
