package io.wexchain.dcc.marketing.domainservice.function.web3;

import org.web3j.crypto.Credentials;
import org.web3j.tx.TransactionManager;

/**
 * CredentialsSupplier
 *
 * @author zhengpeng
 */
public interface CredentialsSupplier {

    Credentials getCredentials(String activityCode);

}
