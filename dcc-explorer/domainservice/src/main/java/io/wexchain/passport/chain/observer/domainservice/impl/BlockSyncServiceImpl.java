package io.wexchain.passport.chain.observer.domainservice.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.passport.chain.observer.common.constant.ChainErrorCode;
import io.wexchain.passport.chain.observer.domain.BlockSync;
import io.wexchain.passport.chain.observer.domainservice.BlockSyncService;
import io.wexchain.passport.chain.observer.repository.BlockSyncRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BlockSyncServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class BlockSyncServiceImpl implements BlockSyncService {

    @Autowired
    private BlockSyncRepository blockSyncRepository;

    @Override
    public BlockSync get(String code) {
        return ErrorCodeValidate.notNull(blockSyncRepository.findOne(code),
                ChainErrorCode.DATA_NOT_FOUND);
    }

    @Override
    public List<BlockSync> getAll() {
        return (List<BlockSync>) blockSyncRepository.findAll();
    }

    @Override
    public BlockSync update(String code, Long blockNumber, String data) {
        BlockSync blockSync = get(code);
        if (blockNumber != null) {
            blockSync.setBlockNumber(blockNumber);
        }
        if (StringUtils.isNotEmpty(data)) {
            blockSync.setData(data);
        }
        return blockSyncRepository.save(blockSync);
    }
}
