package io.wexchain.dcc.marketing.ext.integration.web3;

import org.springframework.beans.factory.FactoryBean;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

/**
 * CredentialsFactoryBean
 *
 * @author zhengpeng
 */
public class CredentialsFactoryBean implements FactoryBean<Credentials> {

    private String password;

    private String filePath;

    @Override
    public Credentials getObject() throws Exception {
        return WalletUtils.loadCredentials(password, filePath);
    }

    @Override
    public Class<?> getObjectType() {
        return Credentials.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
