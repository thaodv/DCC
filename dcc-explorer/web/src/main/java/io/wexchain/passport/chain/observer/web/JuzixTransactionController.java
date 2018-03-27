package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import io.wexchain.passport.chain.observer.common.request.QueryJuzixTransactionRequest;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import io.wexchain.passport.chain.observer.domainservice.JuzixTransactionService;
import io.wexchain.passport.chain.observer.helper.JuzixTransactionResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * JuzixTransactionController
 *
 * @author zhengpeng
 */
@Controller
public class JuzixTransactionController {

    @Autowired
    private JuzixTransactionService juzixTransactionService;

    @Autowired
    private JuzixTransactionResponseHelper juzixTransactionResponseHelper;

    @RequestMapping(value = "/juzix/transaction")
    @ResponseBody
    public Object queryJuzixTransaction(QueryJuzixTransactionRequest queryJuzixTransactionRequet) {
        try {
            Page<JuzixTransaction> page = juzixTransactionService.queryJuzixTransaction(queryJuzixTransactionRequet);
            return juzixTransactionResponseHelper.returnPageSuccess(page);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/juzix/transaction/{hash}")
    @ResponseBody
    public Object getJuzixTransaction(@PathVariable(name = "hash")String hash) {
        try {
            JuzixTransaction juzixTransaction = juzixTransactionService.getJuzixTransactionByHash(hash);
            return juzixTransactionResponseHelper.returnSuccess(juzixTransaction);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/juzix/transaction/data/{hash}")
    @ResponseBody
    public Object getJuzixTransactionData(@PathVariable(name = "hash")String hash) {
        try {
            return ResultResponseUtils
                    .successResultResponse(juzixTransactionService.getTransactionInputData(hash));
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
