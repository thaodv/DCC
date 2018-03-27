package io.wexchain.passport.chain.observer.helper;

import io.wexchain.passport.chain.observer.common.model.JuzixTokenTransferVo;
import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JuzixTokenTransferResponseHelper
        extends ResponseHelper<JuzixTokenTransfer, JuzixTokenTransferVo> {

    {
        setModelClass(JuzixTokenTransferVo.class);
    }

    @Autowired
    @Qualifier("dozerBeanMapper")
    @Override
    public void setMapper(Mapper mapper) {
        super.setMapper(mapper);
    }

}