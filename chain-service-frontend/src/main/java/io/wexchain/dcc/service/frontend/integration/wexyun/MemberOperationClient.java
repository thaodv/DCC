package io.wexchain.dcc.service.frontend.integration.wexyun;

import com.wexyun.open.api.domain.member.Member;
import io.wexchain.dcc.service.frontend.model.param.RegisterParam;

import java.util.List;

public interface MemberOperationClient {

    String register(RegisterParam param);

    String loginPasswordCheck(String loginName);

    Member getByIdentity(String loginName);

    List<Member> queryByInvited(String memberId);
}
