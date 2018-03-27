package io.wexchain.passport.chain.observer.domainservice.impl;

import io.wexchain.passport.chain.observer.domain.TokenLog;
import io.wexchain.passport.chain.observer.domainservice.TokenLogService;
import io.wexchain.passport.chain.observer.domainservice.request.QueryTokenLogRequest;
import io.wexchain.passport.chain.observer.repository.TokenLogRepository;
import io.wexchain.passport.chain.observer.repository.query.TokenLogQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * TokenLogServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class TokenLogServiceImpl implements TokenLogService {

    @Autowired
    private TokenLogRepository tokenLogRepository;

    @Override
    public Page<TokenLog> queryTokenLogPage(QueryTokenLogRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPage() - 1, request.getPageSize(), Sort.Direction.DESC, "createdTime");
        Page<TokenLog> page = tokenLogRepository.findAll(TokenLogQueryBuilder.build(request.getContractAddress(), request.getWalletAddress()), pageRequest);
        return page;
    }

}
