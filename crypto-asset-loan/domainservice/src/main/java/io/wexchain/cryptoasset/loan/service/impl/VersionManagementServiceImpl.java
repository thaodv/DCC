package io.wexchain.cryptoasset.loan.service.impl;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import io.wexchain.cryptoasset.loan.api.constant.AppPlatform;
import io.wexchain.cryptoasset.loan.api.constant.VersionType;
import io.wexchain.cryptoasset.loan.api.model.VersionVO;
import io.wexchain.cryptoasset.loan.api.version.CheckUpgradeRequest;
import io.wexchain.cryptoasset.loan.domain.VersionConfig;
import io.wexchain.cryptoasset.loan.repository.VersionConfigRepository;
import io.wexchain.cryptoasset.loan.service.VersionManagementService;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

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
