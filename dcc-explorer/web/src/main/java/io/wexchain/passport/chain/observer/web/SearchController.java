package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import io.wexchain.passport.chain.observer.common.constant.ChainErrorCode;
import io.wexchain.passport.chain.observer.common.request.PageParam;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import io.wexchain.passport.chain.observer.domain.JuzixTransaction;
import io.wexchain.passport.chain.observer.domainservice.JuzixBlockService;
import io.wexchain.passport.chain.observer.domainservice.JuzixTransactionService;
import io.wexchain.passport.chain.observer.helper.JuzixBlockResponseHelper;
import io.wexchain.passport.chain.observer.helper.JuzixTransactionResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static io.wexchain.passport.chain.observer.common.constant.ChainErrorCode.DATA_NOT_FOUND;
import static io.wexchain.passport.chain.observer.common.constant.ChainErrorCode.TRANSACTION_NOT_FOUND;

/**
 * SearchController
 *
 * @author zhengpeng
 */
@Controller
public class SearchController {

    @Autowired
    private JuzixBlockService juzixBlockService;

    @Autowired
    private JuzixBlockResponseHelper juzixBlockResponseHelper;

    @Autowired
    private JuzixTransactionService juzixTransactionService;

    @Autowired
    private JuzixTransactionResponseHelper juzixTransactionResponseHelper;

    @RequestMapping(value = "/juzix/search/{param}")
    @ResponseBody
    public Object getJuzixBlock(@PathVariable(name = "param")String param) {
        try {
            Map<String, Object> result = new HashMap<>();
            JuzixBlock juzixBlockByNumber = juzixBlockService.getJuzixBlockNullable(param);
            if (juzixBlockByNumber != null) {
                result.put("type", "BLOCK");
                result.put("data", juzixBlockResponseHelper.convert(juzixBlockByNumber));
                return ResultResponseUtils.successResultResponse(result);
            } else {
                JuzixTransaction juzixTransactionByHash = juzixTransactionService.getJuzixTransactionByHashNullable(param);
                if (juzixTransactionByHash != null){
                    result.put("type", "TRANSACTION");
                    result.put("data", juzixTransactionResponseHelper.convert(juzixTransactionByHash));
                    return ResultResponseUtils.successResultResponse(result);
                }
            }
            if (param.matches("^[a-fA-F0-9]{40}$")) {
                result.put("type", "ADDRESS");
                result.put("data", "0x" + param.toLowerCase());
                return ResultResponseUtils.successResultResponse(result);
            }
            if (param.matches("^0[Xx][a-fA-F0-9]{40}$")) {
                result.put("type", "ADDRESS");
                result.put("data", param.toLowerCase());
                return ResultResponseUtils.successResultResponse(result);
            }
            throw new ErrorCodeException(DATA_NOT_FOUND.name(), DATA_NOT_FOUND.getDescription());
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

}
