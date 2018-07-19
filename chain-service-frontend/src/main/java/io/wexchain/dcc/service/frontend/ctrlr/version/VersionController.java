package io.wexchain.dcc.service.frontend.ctrlr.version;

import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.dcc.service.frontend.ctrlr.BaseController;
import io.wexchain.dcc.service.frontend.service.version.VersionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * VersionController
 *
 * @author zhengpeng
 */
@RestController
@Validated
public class VersionController extends BaseController {

    @Autowired
    private VersionService versionService;

    @GetMapping("/version/checkUpgrade")
    public ResultResponse<VersionVO> checkUpgrade(HttpServletRequest request,
                                                  @RequestParam(name = "version") String versionNumber,
                                                  @RequestParam(name = "platform", required = false) String platform) {
        String innerPlatform = platform;

        if (StringUtils.isEmpty(platform)) {
            innerPlatform = "ANDROID";
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String name = (String) headerNames.nextElement();
                String head = request.getHeader(name);
                if (head.toLowerCase().contains("ios")) {
                    innerPlatform = "IOS";
                }
                if (head.toLowerCase().contains("android")) {
                    innerPlatform = "ANDROID";
                }
            }
        }

        return ResultResponseUtils.successResultResponse(versionService.checkUpgrade(versionNumber, innerPlatform));
    }

}
