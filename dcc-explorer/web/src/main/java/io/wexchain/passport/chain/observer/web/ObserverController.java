package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.data.page.PageUtils;
import com.wexmarket.topia.commons.data.rpc.PageTransformer;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.passport.chain.observer.domain.TokenLog;
import io.wexchain.passport.chain.observer.domainservice.TokenLogService;
import io.wexchain.passport.chain.observer.domainservice.request.QueryTokenLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ObserverController
 *
 * @author zhengpeng
 */
@Controller
public class ObserverController {

    @Autowired
    private TokenLogService tokenLogService;

    @RequestMapping(value = "/token/log")
    @ResponseBody
    public Object send(QueryTokenLogRequest request) {
        Page<TokenLog> tokenLogs = tokenLogService.queryTokenLogPage(request);
        Pagination transform = PageTransformer.transform(tokenLogs);
        return ResultResponseUtils.successResultResponse(transform);
    }

}
