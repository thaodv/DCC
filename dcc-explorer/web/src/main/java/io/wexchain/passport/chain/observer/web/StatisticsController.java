package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import io.wexchain.passport.chain.observer.common.model.StatisticsInfo;
import io.wexchain.passport.chain.observer.common.request.PageParam;
import io.wexchain.passport.chain.observer.domain.JuzixBlock;
import io.wexchain.passport.chain.observer.domainservice.JuzixBlockService;
import io.wexchain.passport.chain.observer.domainservice.StatisticsInfoService;
import io.wexchain.passport.chain.observer.helper.JuzixBlockResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * StatisticsController
 *
 * @author zhengpeng
 */
@Controller
public class StatisticsController {

    @Autowired
    private StatisticsInfoService statisticsInfoService;

    @RequestMapping(value = "/juzix/statistics/index")
    @ResponseBody
    public Object queryStatisticsInfo() {
        try {
            StatisticsInfo statisticsInfo = statisticsInfoService.getStatisticsInfo();
            return ResultResponseUtils.successResultResponse(statisticsInfo);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }
}
