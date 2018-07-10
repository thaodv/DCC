package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.domain.CoolDownConfig;

public interface CoolDownConfigService {

    CoolDownConfig getCoolDownConfigByCode(String code);

}
