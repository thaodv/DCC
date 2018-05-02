package io.wexchain.dcc.marketing.ext.service.helper;

import com.wexmarket.topia.commons.data.rpc.ResponseHelper;
import io.wexchain.dcc.marketing.domain.Scenario;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ScenarioResponseHelper
 *
 * @author zhengpeng
 */
@Component
public class ScenarioResponseHelper extends ResponseHelper<Scenario, io.wexchain.dcc.marketing.api.model.Scenario> {

    {
        setModelClass(io.wexchain.dcc.marketing.api.model.Scenario.class);
    }

    @Autowired
    @Qualifier("dozerBeanMapper")
    @Override
    public void setMapper(Mapper mapper) {
        super.setMapper(mapper);
    }

}
