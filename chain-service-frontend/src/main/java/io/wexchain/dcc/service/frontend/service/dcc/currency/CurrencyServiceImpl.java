package io.wexchain.dcc.service.frontend.service.dcc.currency;

import com.wexmarket.topia.commons.basic.config.ConfigReader;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.model.Currency;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Resource(name = "currencyConfigReader")
    private ConfigReader<String, Currency> configReader;

    private Map<String,Currency> currencyMap = new HashMap<>();

    @Override
    public Currency getCurrency(String symbol) {
        Currency currency = ErrorCodeValidate.notNull(currencyMap.get(symbol), FrontendErrorCode.CURRENCY_NOT_EXIST, null);
        return currency;
    }

    @PostConstruct
    public void prepare(){
        if (CollectionUtils.isNotEmpty(configReader.getConfigList())) {
            for (Currency currencyConfig : configReader.getConfigList()) {
                currencyMap.put(currencyConfig.getSymbol(),currencyConfig);
            }
        }
    }
}
