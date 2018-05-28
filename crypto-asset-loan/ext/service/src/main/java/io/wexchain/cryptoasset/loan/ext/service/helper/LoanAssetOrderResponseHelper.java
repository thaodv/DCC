package io.wexchain.cryptoasset.loan.ext.service.helper;

import com.wexmarket.topia.commons.data.rpc.ResponseHelper;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * LoanAssetOrderResponseHelper
 *
 * @author zhengpeng
 */
@Service
public class LoanAssetOrderResponseHelper extends ResponseHelper<LoanOrder, io.wexchain.cryptoasset.loan.api.model.LoanOrder> {

    {
        setModelClass(io.wexchain.cryptoasset.loan.api.model.LoanOrder.class);
    }

    @Autowired
    @Qualifier("dozerBeanMapper")
    @Override
    public void setMapper(Mapper mapper) {
        super.setMapper(mapper);
    }

}
