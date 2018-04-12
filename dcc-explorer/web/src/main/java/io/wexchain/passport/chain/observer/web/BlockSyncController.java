package io.wexchain.passport.chain.observer.web;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import io.wexchain.passport.chain.observer.domain.BlockSync;
import io.wexchain.passport.chain.observer.domainservice.BlockSyncService;
import io.wexchain.passport.chain.observer.helper.BlockSyncResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * BlockSyncController
 *
 * @author zhengpeng
 */
@Controller
public class BlockSyncController {

    @Autowired
    private BlockSyncService blockSyncService;

    @Autowired
    private BlockSyncResponseHelper blockSyncResponseHelper;

    @RequestMapping(value = "/blocksync/getByCode")
    @ResponseBody
    public Object get(@RequestParam("code") String code) {
        try {
            BlockSync blockSync = blockSyncService.get(code);
            return blockSyncResponseHelper.returnSuccess(blockSync);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }

    @RequestMapping(value = "/blocksync/getAll")
    @ResponseBody
    public Object getAll() {
        try {
            List<BlockSync> blockSync = blockSyncService.getAll();
            return blockSyncResponseHelper.returnListSuccess(blockSync);
        } catch (Exception e) {
            return ListResultResponseUtils.exceptionListResultResponse(e);
        }
    }

    @RequestMapping(value = "/blocksync/update")
    @ResponseBody
    public Object update(@RequestParam(name = "blockNumber", required = false) Long blockNumber,
                         @RequestParam(name = "data", required = false) String data,
                         @RequestParam(name = "code")String code) {
        try {
            BlockSync blockSync = blockSyncService.update(code, blockNumber, data);
            return blockSyncResponseHelper.returnSuccess(blockSync);
        } catch (Exception e) {
            return ResultResponseUtils.exceptionResultResponse(e);
        }
    }


}
