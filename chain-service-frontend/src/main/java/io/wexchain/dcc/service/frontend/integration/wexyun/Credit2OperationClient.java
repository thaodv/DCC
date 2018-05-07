package io.wexchain.dcc.service.frontend.integration.wexyun;

import com.wexyun.open.api.domain.credit2.Credit2Apply;
import io.wexchain.dcc.service.frontend.service.wexyun.impl.Credit2ApplyAddRequest;

import java.util.List;

public interface Credit2OperationClient {

    Long apply(Credit2ApplyAddRequest credit2ApplyAddRequest);

    List<Credit2Apply> pageGet(List<String> externalApplyIdList);
}
