package io.wexchain.passport.chain.observer.helper;

import com.wexmarket.topia.commons.data.rpc.ResponseHelper;
import io.wexchain.passport.chain.observer.common.model.BlockSyncVo;
import io.wexchain.passport.chain.observer.domain.BlockSync;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BlockSyncResponseHelper
        extends ResponseHelper<BlockSync, BlockSyncVo> {

    {
        setModelClass(BlockSyncVo.class);
    }

    @Autowired
    @Qualifier("dozerBeanMapper")
    @Override
    public void setMapper(Mapper mapper) {
        super.setMapper(mapper);
    }

}