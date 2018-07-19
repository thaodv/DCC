package io.wexchain.cryptoasset.loan.service;

import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;

/**
 * VersionManagementService
 *
 * @author zhengpeng
 */
public interface VersionManagementService {

    /**
     * 检查版本更新
     * @param request 当前版本号
     * @return 更新对象
     */
    VersionVO checkUpgrade(CheckUpgradeRequest request);

}
