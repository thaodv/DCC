package io.wexchain.dcc.marketing.domainservice.function.wexyun;

import com.wexyun.open.api.domain.member.Member;

public interface WexyunLoanClient {

    Member getMemberByIdentity(String address);

    Member getMemberById(String memberId);

}
