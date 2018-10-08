package io.wexchain.cryptoasset.loan.service;

import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;
import io.wexchain.cryptoasset.loan.api.version.QueryVersionConfigPageRequest;
import io.wexchain.cryptoasset.loan.api.version.SaveVersionRequest;
import io.wexchain.cryptoasset.loan.api.version.VersionIndex;
import io.wexchain.cryptoasset.loan.domain.VersionConfig;

import org.springframework.data.domain.Page;

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

    /**
     * 根据版本配置信息分页查询
     * @param request
     * @return 更新对象
     */
    Page<VersionConfig> queryVersionConfig(QueryVersionConfigPageRequest request);

    /**
     * 保存版本配置
     * @param request
     * @return
     */
    VersionConfig saveVersionConfig(SaveVersionRequest request);

    /**
     * 根据id查询版本配置
     * @param id
     * @return 更新对象
     */
    VersionConfig getVersionConfig(Long id);

    /**
     * 删除版本配置
     * @param id
     * @return 更新对象
     */
    void deleteVersionConfig(Long id);


    VersionConfig getVersionConfigByIndex(VersionIndex index);
}
