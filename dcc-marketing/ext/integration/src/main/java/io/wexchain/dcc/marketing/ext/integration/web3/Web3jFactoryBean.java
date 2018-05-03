package io.wexchain.dcc.marketing.ext.integration.web3;

import juzix.web3j.protocol.CustomWeb3j;
import juzix.web3j.protocol.CustomWeb3jFactory;
import org.springframework.beans.factory.FactoryBean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Web3jFactoryBean
 *
 * @author zhengpeng
 */
public class Web3jFactoryBean implements FactoryBean<CustomWeb3j> {

    private String web3Url;

    @Override
    public CustomWeb3j getObject() throws Exception {
        return CustomWeb3jFactory.buildweb3j(new HttpService(web3Url));
    }

    @Override
    public Class<?> getObjectType() {
        return CustomWeb3j.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getWeb3Url() {
        return web3Url;
    }

    public void setWeb3Url(String web3Url) {
        this.web3Url = web3Url;
    }
}
