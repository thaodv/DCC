package io.wexchain.dcc.marketing.domainservice.function.web3.impl;

import io.wexchain.dcc.marketing.domainservice.function.web3.CredentialsSupplier;
import io.wexchain.dcc.marketing.repository.ActivityRepository;
import juzix.web3j.NonceRawTransactionManager;
import juzix.web3j.protocol.CustomWeb3j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.tx.TransactionManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * CredentialsSupplier
 *
 * @author zhengpeng
 */
@Component
public class CredentialsSupplierImpl implements CredentialsSupplier {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActivityRepository activityRepository;

    @Value("${credential.marketing.source.10001}")
    private String path;

    @Value("${credential.marketing.password.10001}")
    private String password;

    private Map<String, Credentials> credentialsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        /*activityRepository.findAll().forEach(activity -> {
            try {
                String path = environment.getProperty("credential.marketing.source" + activity.getCode());
                String password = environment.getProperty("credential.marketing.password" + activity.getCode());
                Credentials credentials = WalletUtils.loadCredentials(password, path);
                credentialsMap.put(activity.getCode(), credentials);
            } catch (Exception e) {
                logger.warn("Create credential fail, activity code:{}", activity.getCode(), e);
            }
        });*/
        try {
            Credentials credentials = WalletUtils.loadCredentials(password, path);
            credentialsMap.put("10001", credentials);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Credentials getCredentials(String activityCode) {
        return credentialsMap.get(activityCode);
    }

}
