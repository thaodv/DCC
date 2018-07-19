package io.wexchain.passport.chain.observer.domainservice;

import io.wexchain.passport.chain.observer.common.request.PageParam;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import org.springframework.data.domain.Page;

public interface JuzixBlockService {

    JuzixBlock getJuzixBlock(String search);

    Long getJuzixBlockNumber();

    JuzixBlock getJuzixBlockNullable(String search);

    JuzixBlock getJuzixBlockById(Long id);

    JuzixBlock getJuzixBlockByNumber(Long number);

    JuzixBlock getJuzixBlockByHash(String hash);

    Page<JuzixBlock> queryJuzixBlock(PageParam request);

    Long getLatestGeneBlockTime();

    int fixBlock();


}
