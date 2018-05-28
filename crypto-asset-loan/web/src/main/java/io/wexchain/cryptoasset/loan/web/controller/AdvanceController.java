package io.wexchain.cryptoasset.loan.web.controller;

import com.wexmarket.topia.commons.basic.rpc.utils.BaseResponseUtils;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdvanceController
 *
 * @author zhengpeng
 */
@RestController
public class AdvanceController {

    @Autowired
    private CryptoAssetLoanService cryptoAssetLoanService;

    @GetMapping("/loanOrder/advance")
    public BaseResponse pass(@RequestParam("orderId")Long orderId) {
        cryptoAssetLoanService.advance(orderId);
        return BaseResponseUtils.successBaseResponse();
    }
}
