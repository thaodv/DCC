package io.wexchain.dcc.service.frontend.ctrlr;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.dcc.service.frontend.model.vo.BankInfoVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * BankListController
 *
 * @author zhengpeng
 */
@RestController
public class BankListController extends BaseController {

    private static final List<BankInfoVo> banks;

    static {
        banks = new ArrayList<BankInfoVo>();
        banks.add(new BankInfoVo("ABC", "中国农业银行"));
        banks.add(new BankInfoVo("CCB", "中国建设银行"));
        banks.add(new BankInfoVo("CITIC", "中信银行"));
        banks.add(new BankInfoVo("GDB", "广东发展银行"));
        banks.add(new BankInfoVo("CEB", "中国光大银行"));
        banks.add(new BankInfoVo("SZPAB", "平安银行"));
        banks.add(new BankInfoVo("BOC", "中国银行"));
        banks.add(new BankInfoVo("ICBC", "中国工商银行"));
        banks.add(new BankInfoVo("CMB", "招商银行"));
        banks.add(new BankInfoVo("CMBC", "中国民生银行"));
        banks.add(new BankInfoVo("CIB", "兴业银行"));
        banks.add(new BankInfoVo("SPDB", "浦发银行"));
        banks.add(new BankInfoVo("BOS", "上海银行"));
        banks.add(new BankInfoVo("HXB", "华夏银行"));
        banks.add(new BankInfoVo("KSRCB", "昆山农商银行"));
    }

    @GetMapping("/bank/list")
    public ResultResponse<List<BankInfoVo>> queryBankList() {
        return ResultResponseUtils.successResultResponse(banks);
    }


}
