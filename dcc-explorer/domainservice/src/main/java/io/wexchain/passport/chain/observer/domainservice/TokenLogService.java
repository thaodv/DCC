package io.wexchain.passport.chain.observer.domainservice;

import io.wexchain.passport.chain.observer.domain.TokenLog;
import io.wexchain.passport.chain.observer.domainservice.request.QueryTokenLogRequest;
import org.springframework.data.domain.Page;

/**
 * TokenLogService
 *
 * @author zhengpeng
 */
public interface TokenLogService {

    Page<TokenLog> queryTokenLogPage(QueryTokenLogRequest request);

}
