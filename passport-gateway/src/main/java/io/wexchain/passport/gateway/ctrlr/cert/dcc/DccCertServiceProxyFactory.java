package io.wexchain.passport.gateway.ctrlr.cert.dcc;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.passport.gateway.ctrlr.cert.CertErrorCode;
import io.wexchain.passport.gateway.service.cert.dcc.DccCertServiceProxy;

import java.util.Map;

/**
 * <p>
 * 认证代理器工厂
 * </p>
 * 
 * @author yanyi
 * @version $Id: CertServiceProxyFactory.java, yanyi Exp $
 */

public class DccCertServiceProxyFactory {
    /**
     * 业务类型 资料认证代理器射关系
     */
    private Map<String, DccCertServiceProxy> certServiceProxyMap;

    public DccCertServiceProxy getCertProxy(String business) {
        DccCertServiceProxy certServiceProxy = certServiceProxyMap.get(business);
        return ErrorCodeValidate.notNull(certServiceProxy, CertErrorCode.BUSINESS_NOT_FOUND);
    }

    public Map<String, DccCertServiceProxy> getCertServiceProxyMap() {
        return certServiceProxyMap;
    }

    public void setCertServiceProxyMap(Map<String, DccCertServiceProxy> certServiceProxyMap) {
        this.certServiceProxyMap = certServiceProxyMap;
    }
}
