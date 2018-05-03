package io.wexchain.dcc.service.frontend.integration.wexyun;

import com.wexyun.open.api.domain.credit2.Credit2Apply;
import com.wexyun.open.api.request.credit.credit2.Credit2ApplyAddRequest;

import java.util.List;

public interface Credit2OperationClient {

    Long apply(Credit2ApplyAddRequest credit2ApplyAddRequest);

    List<Credit2Apply> pageGet(List<String> externalApplyIdList);
}
