package io.wexchain.dcc.service.frontend.service.dcc.lender;

import com.wexmarket.topia.commons.basic.config.ConfigReader;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.model.Lender;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LenderServiceImpl implements LenderService {

    @Resource(name = "lenderConfigReader")
    private ConfigReader<String, Lender> configReader;

    private Map<String,Lender> lenderConfigMap = new HashMap<>();

    private Lender defaultLender;

    @PostConstruct
    public void prepare(){
        if (CollectionUtils.isNotEmpty(configReader.getConfigList())) {
            for (Lender lender : configReader.getConfigList()) {
                if(lender.isDefaultConfig()){
                    defaultLender = lender;
                }
                lenderConfigMap.put(lender.getCode(),lender);
            }
        }
    }

    @Override
    public Lender getLender(String code) {
        Lender lender = ErrorCodeValidate.notNull(lenderConfigMap.get(code), FrontendErrorCode.LENDER_NOT_EXIST, null);
        return lender;
    }

    @Override
    public Lender getDefaultLender() {
        return defaultLender;
    }

    @Override
    public List<Lender> getLenderList() {
        return configReader.getConfigList();
    }
}
