package io.wexchain.dcc.service.frontend.integration.version.impl;

import com.weihui.basic.util.integration.IntegrationProxy;
import com.wexmarket.topia.commons.rpc.ResultResponse;
import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;
import io.wexchain.cryptoasset.loan.api.version.VersionManagementFacade;
import io.wexchain.dcc.service.frontend.integration.common.ExecuteTemplate;
import io.wexchain.dcc.service.frontend.integration.version.VersionManagementOperationClient;
import io.wexchain.dcc.service.frontend.utils.ResultResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * VersionManagementOperationClientImpl
 *
 * @author zhengpeng
 */
@Component
public class VersionManagementOperationClientImpl implements VersionManagementOperationClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "versionManagementFacade")
    private IntegrationProxy<VersionManagementFacade> versionManagementFacade;

    @Override
    public VersionVO checkUpgrade(CheckUpgradeRequest request) {
        ResultResponse<VersionVO> resultResponse =
                ExecuteTemplate.execute(()
                -> versionManagementFacade.buildInst().checkUpgrade(request), logger, "查询版本更新", request);
        return ResultResponseValidator.getResult(resultResponse);
    }

}
