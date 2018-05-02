package io.wexchain.dcc.service.frontend.service.dcc.loanProduct;

import com.weihui.basic.lang.common.lang.StringUtil;
import com.wexmarket.topia.commons.basic.config.ConfigReader;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.dcc.service.frontend.common.enums.FrontendErrorCode;
import io.wexchain.dcc.service.frontend.model.Currency;
import io.wexchain.dcc.service.frontend.model.Lender;
import io.wexchain.dcc.service.frontend.model.LoanProduct;
import io.wexchain.dcc.service.frontend.model.vo.LoanProductVo;
import io.wexchain.dcc.service.frontend.service.dcc.currency.CurrencyService;
import io.wexchain.dcc.service.frontend.service.dcc.lender.LenderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoanProductServiceImpl implements LoanProductService {

    @Resource(name = "loanProductConfigReader")
    private ConfigReader<String, LoanProduct> configReader;

    private Map<Long,LoanProduct> productByIdMap = new HashMap<>();
    private Map<String,List<LoanProduct>> productByLenderCodeMap = new HashMap<>();

    @Autowired
    private LenderService lenderService;
    @Autowired
    private CurrencyService currencyService;

    @PostConstruct
    public void prepare(){
        if (CollectionUtils.isNotEmpty(configReader.getConfigList())) {
            for (LoanProduct loanProduct : configReader.getConfigList()) {
                productByIdMap.put(loanProduct.getId(),loanProduct);

                if(productByLenderCodeMap.get(loanProduct.getLenderCode()) != null){
                    productByLenderCodeMap.get(loanProduct.getLenderCode()).add(loanProduct);
                }else {
                    List<LoanProduct> loanProducts = new ArrayList<>();
                    loanProducts.add(loanProduct);
                    productByLenderCodeMap.put(loanProduct.getLenderCode(),loanProducts);
                }
            }
        }
    }

    @Override
    public LoanProductVo getLoanProductVo(Long id) {
        LoanProduct loanProduct = ErrorCodeValidate.notNull(productByIdMap.get(id), FrontendErrorCode.LOAN_PRODUCT_NOT_EXIST, null);
        LoanProductVo loanProductVo = new LoanProductVo(loanProduct);
        loanProductVo.setLender(lenderService.getLender(loanProduct.getLenderCode()));
        loanProductVo.setCurrency(currencyService.getCurrency(loanProduct.getCurrencySymbol()));
        return loanProductVo;
    }

    @Override
    public List<LoanProductVo> getLoanProductVoList(String lenderCode) {
        if(StringUtil.isBlank(lenderCode)){
            lenderCode = lenderService.getDefaultLender().getCode();
        }
        List<LoanProduct> loanProducts = ErrorCodeValidate.notNull(productByLenderCodeMap.get(lenderCode), FrontendErrorCode.LOAN_PRODUCT_NOT_EXIST, null);
        List<LoanProductVo> loanProductVos = new ArrayList<>();
        for (LoanProduct loanProduct : loanProducts) {
            LoanProductVo loanProductVo = new LoanProductVo(loanProduct);
            loanProductVo.setLender(lenderService.getLender(loanProduct.getLenderCode()));
            loanProductVo.setCurrency(currencyService.getCurrency(loanProduct.getCurrencySymbol()));
            loanProductVos.add(loanProductVo);
        }
        return loanProductVos;
    }
}
