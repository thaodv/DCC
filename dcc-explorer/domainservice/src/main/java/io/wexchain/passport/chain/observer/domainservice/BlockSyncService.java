package io.wexchain.passport.chain.observer.domainservice;

import io.wexchain.passport.chain.observer.domain.BlockSync;

import java.util.List;

public interface BlockSyncService {

    BlockSync get(String code);

    List<BlockSync> getAll();

    BlockSync update(String code, Long blockNumber, String data);

}
