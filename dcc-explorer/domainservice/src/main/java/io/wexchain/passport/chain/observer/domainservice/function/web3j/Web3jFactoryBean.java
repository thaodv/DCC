package io.wexchain.passport.chain.observer.domainservice.function.web3j;

import org.springframework.beans.factory.FactoryBean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Web3jFactoryBean
 *
 * @author zhengpeng
 */
public class Web3jFactoryBean implements FactoryBean<Web3j> {

    private String web3Url;

    @Override
    public Web3j getObject() throws Exception {
        return Web3j.build(new HttpService(web3Url));
    }

    @Override
    public Class<?> getObjectType() {
        return Web3j.class;
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
