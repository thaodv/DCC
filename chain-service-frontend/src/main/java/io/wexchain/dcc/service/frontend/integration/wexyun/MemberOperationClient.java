package io.wexchain.dcc.service.frontend.integration.wexyun;

import io.wexchain.dcc.service.frontend.model.param.RegisterParam;

public interface MemberOperationClient {

    String register(RegisterParam param);

    String loginPasswordCheck(String loginName);

    String getByIdentity(String loginName);
}
