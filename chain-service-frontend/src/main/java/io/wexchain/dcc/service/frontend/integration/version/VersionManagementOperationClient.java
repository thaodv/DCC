package io.wexchain.dcc.service.frontend.integration.version;

import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;

/**
 * VersionManagementOperationClient
 *
 * @author zhengpeng
 */
public interface VersionManagementOperationClient {

    VersionVO checkUpgrade(CheckUpgradeRequest request);
}
