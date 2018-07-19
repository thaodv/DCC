package io.wexchain.dcc.service.frontend.service.version;

import io.wexchain.cryptoasset.loan.api.model.VersionVO;

/**
 * VersionService
 *
 * @author zhengpeng
 */
public interface VersionService {

    VersionVO checkUpgrade(String versionNumber, String platform);
}
