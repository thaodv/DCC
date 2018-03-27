package io.wexchain.passport.chain.observer.helper;

import io.wexchain.passport.chain.observer.common.model.JuzixTransactionVo;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JuzixTransactionResponseHelper
        extends ResponseHelper<JuzixTransaction, JuzixTransactionVo> {

    {
        setModelClass(JuzixTransactionVo.class);
    }

    public JuzixTransactionVo convert(JuzixTransaction juzixTransaction) {
        return this.returnSuccess(juzixTransaction).getResult();
    }

    @Autowired
    @Qualifier("dozerBeanMapper")
    @Override
    public void setMapper(Mapper mapper) {
        super.setMapper(mapper);
    }

}