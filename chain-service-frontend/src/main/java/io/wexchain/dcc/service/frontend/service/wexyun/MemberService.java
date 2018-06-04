package io.wexchain.dcc.service.frontend.service.wexyun;

import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface MemberService {

    String register(RegisterRequest request);

    String registerAndLogin(RegisterRequest registerRequest, HttpServletRequest request);

    String loginPasswordCheck(String loginName);

    Member getByIdentity(String loginName);

    List<Member> queryByInvited(String memberId);
}
