package io.wexchain.dcc.service.frontend.service.wexyun;

import io.wexchain.dcc.service.frontend.model.request.RegisterRequest;

/**
 * IdHashService
 *
 * @author zhengpeng
 */
public interface MemberService {

    String register(RegisterRequest request);

    String loginPasswordCheck(String loginName);

    String getByIdentity(String loginName);
}
