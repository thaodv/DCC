package io.wexchain.cryptoasset.loan.service.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.data.page.PageUtils;

import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.api.constant.VersionType;
import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;
import io.wexchain.cryptoasset.loan.api.version.QueryVersionConfigPageRequest;
import io.wexchain.cryptoasset.loan.api.version.SaveVersionRequest;
import io.wexchain.cryptoasset.loan.api.version.VersionIndex;
import io.wexchain.cryptoasset.loan.domain.VersionConfig;
import io.wexchain.cryptoasset.loan.repository.VersionConfigRepository;
import io.wexchain.cryptoasset.loan.repository.query.VersionConfigQueryBuilder;
import io.wexchain.cryptoasset.loan.service.VersionManagementService;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

/**
 * VersionManagementServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class VersionManagementServiceImpl implements VersionManagementService {

    @Autowired
    private VersionConfigRepository versionConfigRepository;

    @Override
    public VersionVO checkUpgrade(CheckUpgradeRequest request) {

        VersionConfig versionConfig = versionConfigRepository
                .findFirstByPlatformOrderByReleaseTimeDesc(request.getPlatform());

        if (versionConfig == null) {
            throw new ErrorCodeException("VERSION_NOT_FOUND", "版本信息未找到");
        }

        int compareVersionResult = compareVersion(request.getVersionNumber(), versionConfig.getVersion(),
                versionConfig.getVersionType());
        if (compareVersionResult == 0) {
            return null;
        }
        if (compareVersionResult > 0) {
            throw new ErrorCodeException("INVALID_VERSION", "无效的版本号");
        }

        boolean needUpdate = compareVersion(request.getVersionNumber(), versionConfig.getMinCompatibilityVersion(),
                versionConfig.getVersionType()) < 0;

        VersionVO vo = new VersionVO();
        vo.setUpdateUrl(versionConfig.getUpdateUrl());
        vo.setVersion(versionConfig.getVersionShow());
        vo.setReleaseTime(new DateTime(versionConfig.getReleaseTime()));
        vo.setUpdateLog(versionConfig.getUpdateLog());
        vo.setMandatoryUpgrade(needUpdate);

        return vo;
    }

    @Override
    public Page<VersionConfig> queryVersionConfig(QueryVersionConfigPageRequest request) {
        PageRequest pageRequest = PageUtils.convert(request.getSortPageParam());
        return versionConfigRepository.findAll(VersionConfigQueryBuilder.query(request), pageRequest);
    }

    @Override
    public VersionConfig saveVersionConfig(SaveVersionRequest request) {
        VersionConfig versionConfig = new VersionConfig();

        if (request.getId() == null) {  //新增流程
            //判断平台、版本号唯一
            QueryVersionConfigPageRequest configRequest = new QueryVersionConfigPageRequest();
            configRequest.setAppPlatform(request.getAppPlatform());
            configRequest.setVersion(request.getVersion());
            List<VersionConfig> versionConfigs = versionConfigRepository.findAll(VersionConfigQueryBuilder.query(configRequest));  //查询平台和版本号是否唯一
            ErrorCodeValidate.isTrue(versionConfigs.size() == 0, CalErrorCode.VERSION_CONFIG_EXIST);

            //新增属性
            versionConfig.setPlatform(request.getAppPlatform());
            versionConfig.setVersionShow(request.getVersionShow());
            versionConfig.setVersion(request.getVersion());
            versionConfig.setMinCompatibilityVersion(request.getMinCompatibilityVersion());
            versionConfig.setVersionType(request.getVersionType());
            versionConfig.setUpdateLog(request.getUpdateLog());
            versionConfig.setUpdateUrl(request.getUpdateUrl());
            versionConfig.setReleaseTime(request.getReleaseTime().toDate());
            versionConfig.setMemo(request.getMemo());
            return versionConfigRepository.save(versionConfig);
        } else {  //更新
            VersionConfig versionConfigExist = getVersionConfig(request.getId());
            ErrorCodeValidate.isTrue(versionConfigExist != null, CalErrorCode.VERSION_CONFIG_NOTEXIST);

            versionConfig.setId(request.getId());
            versionConfig.setPlatform(request.getAppPlatform());
            versionConfig.setVersionShow(request.getVersionShow());
            versionConfig.setVersion(request.getVersion());
            versionConfig.setMinCompatibilityVersion(request.getMinCompatibilityVersion());
            versionConfig.setVersionType(request.getVersionType());
            versionConfig.setUpdateLog(request.getUpdateLog());
            versionConfig.setUpdateUrl(request.getUpdateUrl());
            versionConfig.setReleaseTime(request.getReleaseTime().toDate());
            versionConfig.setMemo(request.getMemo());
            versionConfig.setDataVersion(versionConfigExist.getDataVersion());
            try {
                return versionConfigRepository.save(versionConfig);
            } catch (Exception e) {
                throw new ErrorCodeException("SAVE_VERSION_FAIL", "版本配置已存在");
            }
        }
    }

    @Override
    public VersionConfig getVersionConfig(Long id) {
        Optional<VersionConfig> findById = versionConfigRepository.findById(id);
        return findById.isPresent() ? findById.get() : null;
    }

    @Override
    public void deleteVersionConfig(Long id) {
        versionConfigRepository.deleteById(id);
    }

    @Override
    public VersionConfig getVersionConfigByIndex(VersionIndex index) {
        return ErrorCodeValidate.notNull(
                versionConfigRepository.findByVersionAndPlatform(index.getVersionNumber(), index.getPlatform()),
                CalErrorCode.VERSION_CONFIG_NOTEXIST);
    }

    private int compareVersion(String versionA, String versionB, VersionType versionType) {
        switch (versionType) {
            case DOT_SEPARATOR_NUMBER: {
                String[] splitA = versionA.split("\\.");
                String[] splitB = versionB.split("\\.");
                for (int i = 0; i < Math.min(splitA.length, splitB.length); i++) {
                    int compare = Integer.valueOf(splitA[i]).compareTo(Integer.valueOf(splitB[i]));
                    if (compare == 0) {
                        continue;
                    }
                    return compare;
                }
                return 0;
            }
            case NUMBER: {
                return Integer.valueOf(versionA).compareTo(Integer.valueOf(versionB));
            }
            default: {
                return 0;
            }
        }
    }

}
