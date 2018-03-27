package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import io.wexchain.passport.chain.observer.common.request.QueryJuzixTokenTransferRequest;
import io.wexchain.passport.chain.observer.domain.JuzixTokenTransfer;
import io.wexchain.passport.chain.observer.domainservice.JuzixTokenTransferService;
import io.wexchain.passport.chain.observer.helper.JuzixTokenTransferResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;

/**
 * JuzixTransactionController
 *
 * @author zhengpeng
 */
@Controller
public class JuzixTokenTransferController {

    @Autowired
    private JuzixTokenTransferService juzixTokenTransferService;

    @Autowired
    private JuzixTokenTransferResponseHelper juzixTokenTransferResponseHelper;

    @RequestMapping(value = "/juzix/tokenTransfer/dcc")
    @ResponseBody
    public Object queryDccTokenTransfer(QueryJuzixTokenTransferRequest request) {
        try {
            Page<JuzixTokenTransfer> page = juzixTokenTransferService.queryDccTransfer(request);
            return juzixTokenTransferResponseHelper.returnPageSuccess(page);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/juzix/tokenTransfer")
    @ResponseBody
    public Object queryTokenTransfer(QueryJuzixTokenTransferRequest request) {
        try {
            Page<JuzixTokenTransfer> page = juzixTokenTransferService.queryJuzixTokenTransfer(request);
            return juzixTokenTransferResponseHelper.returnPageSuccess(page);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/juzix/tokenBalance/dcc/{address}")
    @ResponseBody
    public Object getBalance(@PathVariable(name = "address")String address) {
        try {
            BigInteger balance = juzixTokenTransferService.getDccBalance(address);
            return ResultResponseUtils.successResultResponse(balance.toString());
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
