package io.wexchain.passport.gateway.ctrlr.cert;

import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import io.wexchain.passport.gateway.service.cert.CertServiceProxy;

import java.util.Map;

/**
 * <p>
 * 资料认证代理器工厂
 * </p>
 * 
 * @author yanyi
 * @version $Id: CertServiceProxyFactory.java, yanyi Exp $
 */

public class CertServiceProxyFactory {
    /**
     * 业务类型 资料认证代理器射关系
     */
    private Map<String, CertServiceProxy> certServiceProxyMap;

    public CertServiceProxy getCertProxy(String business) {
        CertServiceProxy certServiceProxy = certServiceProxyMap.get(business);
        return ErrorCodeValidate.notNull(certServiceProxy, CertErrorCode.BUSINESS_NOT_FOUND);
    }

    public Map<String, CertServiceProxy> getCertServiceProxyMap() {
        return certServiceProxyMap;
    }

    public void setCertServiceProxyMap(Map<String, CertServiceProxy> certServiceProxyMap) {
        this.certServiceProxyMap = certServiceProxyMap;
    }
}
