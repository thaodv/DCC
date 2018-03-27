package io.wexchain.passport.chain.observer.domainservice.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.passport.chain.observer.common.constant.ChainErrorCode;
import io.wexchain.passport.chain.observer.common.request.PageParam;
import io.wexchain.passport.chain.observer.common.util.PageUtil;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import io.wexchain.passport.chain.observer.domainservice.JuzixBlockService;
import io.wexchain.passport.chain.observer.repository.JuzixBlockRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.io.IOException;

/**
 * JuzixBlockServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class JuzixBlockServiceImpl implements JuzixBlockService {

    @Autowired
    private JuzixBlockRepository juzixBlockRepository;

    @Autowired
    private Web3j web3j;

    @Override
    public JuzixBlock getJuzixBlock(String search) {
        if (StringUtils.isNumeric(search)) {
            return getJuzixBlockByNumber(Long.valueOf(search));
        }
        if (search.matches("^0?[xX]?[a-fA-F0-9]+$")) {
            return getJuzixBlockByHash(search.toLowerCase());
        }
        return null;
    }

    @Override
    public Long getJuzixBlockNumber() {
        try {
            return web3j.ethBlockNumber().send().getBlockNumber().longValue();
        } catch (IOException e) {
            throw new RuntimeException("Get block number fail", e);
        }
    }

    @Override
    public JuzixBlock getJuzixBlockNullable(String search) {
        if (StringUtils.isNumeric(search)) {
            return juzixBlockRepository.findByBlockNumber(Long.valueOf(search));
        }
        if (search.matches("^[a-fA-F0-9]+$")) {
            return juzixBlockRepository.findByHash("0x" + search.toLowerCase());
        }
        if (search.matches("^0[xX][a-fA-F0-9]+$")) {
            return juzixBlockRepository.findByHash(search.toLowerCase());
        }
        return null;
    }

    @Override
    public JuzixBlock getJuzixBlockById(Long id) {
        return ErrorCodeValidate.notNull(juzixBlockRepository.findOne(id), ChainErrorCode.BLOCK_NOT_FOUND);
    }

    @Override
    public JuzixBlock getJuzixBlockByNumber(Long number) {
        return ErrorCodeValidate.notNull(
                juzixBlockRepository.findByBlockNumber(number), ChainErrorCode.BLOCK_NOT_FOUND);
    }

    @Override
    public JuzixBlock getJuzixBlockByHash(String hash) {
        String lowerCaseHash = hash.toLowerCase();
        if (!lowerCaseHash.startsWith("0x")) {
            lowerCaseHash = "0x" + lowerCaseHash;
        }
        return ErrorCodeValidate.notNull(
                juzixBlockRepository.findByHash(lowerCaseHash), ChainErrorCode.BLOCK_NOT_FOUND);
    }

    @Override
    public Page<JuzixBlock> queryJuzixBlock(PageParam pageParam) {
        PageRequest pageRequest = PageUtil.convert(pageParam, new Sort(Sort.Direction.DESC, "blockTimestamp"));
        return juzixBlockRepository.findAll(pageRequest);
    }

    @Override
    public Long getLatestGeneBlockTime() {
        Long maxBlockNumber = juzixBlockRepository.findMaxNumber();
        if (maxBlockNumber == null || maxBlockNumber.equals(0L)) {
            return 0L;
        }
        JuzixBlock currentBlock = juzixBlockRepository.findByBlockNumber(maxBlockNumber);
        JuzixBlock preBlock = juzixBlockRepository.findByBlockNumber(maxBlockNumber - 1);
        return currentBlock.getBlockTimestamp().getTime() - preBlock.getBlockTimestamp().getTime();
    }
}
