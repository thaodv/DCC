package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.data.rpc.PageTransformer;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.passport.chain.observer.common.request.PageParam;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import io.wexchain.passport.chain.observer.domain.TokenLog;
import io.wexchain.passport.chain.observer.domainservice.JuzixBlockService;
import io.wexchain.passport.chain.observer.domainservice.TokenLogService;
import io.wexchain.passport.chain.observer.helper.JuzixBlockResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * JuzixBlockController
 *
 * @author zhengpeng
 */
@Controller
public class JuzixBlockController {

    @Autowired
    private JuzixBlockService juzixBlockService;

    @Autowired
    private JuzixBlockResponseHelper juzixBlockResponseHelper;

    @RequestMapping(value = "/juzix/block")
    @ResponseBody
    public Object queryJuzixBlock(PageParam pageParam) {
        try {
            Page<JuzixBlock> page = juzixBlockService.queryJuzixBlock(pageParam);
            return juzixBlockResponseHelper.returnPageSuccess(page);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/juzix/block/{search}")
    @ResponseBody
    public Object getJuzixBlock(@PathVariable(name = "search")String search) {
        try {
            JuzixBlock juzixBlock = juzixBlockService.getJuzixBlock(search);
            return juzixBlockResponseHelper.returnSuccess(juzixBlock);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/juzix/block/number")
    @ResponseBody
    public Object getJuzixBlockNumber() {
        try {
            return ResultResponseUtils.successResultResponse(juzixBlockService.getJuzixBlockNumber());
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }


}
