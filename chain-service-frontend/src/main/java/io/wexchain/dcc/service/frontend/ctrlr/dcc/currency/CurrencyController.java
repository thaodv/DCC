package io.wexchain.dcc.service.frontend.ctrlr.dcc.currency;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.ctrlr.BaseController;
import io.wexchain.dcc.service.frontend.service.dcc.currency.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


/**
 * CurrencyController
 *
 * @author zhengpeng
 */
@RestController
@RequestMapping("/currency")
@Validated
public class CurrencyController extends BaseController {

    @Autowired
    private CurrencyService currencyConfig;

    @GetMapping("/get")
    public ResultResponse<Currency> get(@NotBlank(message = "symbol不能为空") String symbol) {
        Currency currency = currencyConfig.getCurrency(symbol);
        return ResultResponseUtils.successResultResponse(currency);
    }
}
