package io.wexchain.dcc.service.frontend.service.version.impl;

import io.wexchain.cryptoasset.loan.api.constant.AppPlatform;
import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;
import io.wexchain.dcc.service.frontend.integration.version.VersionManagementOperationClient;
import io.wexchain.dcc.service.frontend.service.version.VersionService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * VersionService
 *
 * @author zhengpeng
 */
public class VersionServiceImpl implements VersionService {

    @Autowired
    private VersionManagementOperationClient versionManagementOperationClient;

    @Override
    public VersionVO checkUpgrade(String versionNumber, String platform) {

        CheckUpgradeRequest request = new CheckUpgradeRequest();
        request.setVersionNumber(versionNumber);
        request.setPlatform(AppPlatform.valueOf(platform.toUpperCase()));

        return versionManagementOperationClient.checkUpgrade(request);
    }
}
