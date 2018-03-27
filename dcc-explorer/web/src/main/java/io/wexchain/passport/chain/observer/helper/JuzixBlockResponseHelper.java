package io.wexchain.passport.chain.observer.helper;

import com.wexmarket.topia.commons.data.rpc.ResponseHelper;
import io.wexchain.passport.chain.observer.common.model.JuzixBlockVo;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JuzixBlockResponseHelper
        extends ResponseHelper<JuzixBlock, JuzixBlockVo> {

    {
        setModelClass(JuzixBlockVo.class);
    }

    public JuzixBlockVo convert(JuzixBlock juzixBlock) {
        return this.returnSuccess(juzixBlock).getResult();
    }

    @Autowired
    @Qualifier("dozerBeanMapper")
    @Override
    public void setMapper(Mapper mapper) {
        super.setMapper(mapper);
    }

}