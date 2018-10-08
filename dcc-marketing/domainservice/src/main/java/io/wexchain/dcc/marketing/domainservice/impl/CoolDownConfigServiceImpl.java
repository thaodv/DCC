package io.wexchain.dcc.marketing.domainservice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;

import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.domain.CoolDownConfig;
import io.wexchain.dcc.marketing.domainservice.CoolDownConfigService;
import io.wexchain.dcc.marketing.repository.CoolDownConfigRepository;

/**
 * CoolDownConfigServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class CoolDownConfigServiceImpl implements CoolDownConfigService {

    @Autowired
    private CoolDownConfigRepository coolDownConfigRepository;

    @Override
    public CoolDownConfig getCoolDownConfigByCode(String code) {
        return ErrorCodeValidate.notNull(
                coolDownConfigRepository.findByCode(code),
                MarketingErrorCode.CD_CONFIG_NOT_FOUND);
    }
}
