package io.wexchain.dcc.marketing.ext.integration.configuration;

import com.wexyun.open.api.client.ConfigValues;
import com.wexyun.open.api.client.DefaultWexyunApiClient;
import com.wexyun.open.api.client.WexyunApiClient;
import com.wexyun.open.api.enums.log.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LoanSdkConfiguration
 *
 * @author zhengpeng
 */
@Configuration
public class WexyunConfiguration {

    @Value("${wexyun.api.partnerId}")
    private String partnerId;

    @Value("${wexyun.api.remoteUrl}")
    private String remoteUrl;

    @Value("${wexyun.api.md5Salt}")
    private String md5Salt;

    @Value("${wexyun.api.rsaEncryptPublicKey}")
    private String rsaEncryptPublicKey;

    @Bean(name = "wexyunApiClient")
    public WexyunApiClient wexyunApiClient() {
        ConfigValues configValues = new ConfigValues();
        configValues.setPartnerId(partnerId);
        configValues.setRemoteUrl(remoteUrl);
        configValues.setMd5Salt(md5Salt);
        configValues.setRsaEncryptPublicKey(rsaEncryptPublicKey);
        configValues.setLogOpen(false);
        configValues.setLogLevel(LogLevel.INFO);
        configValues.setConnTimeout(30000);
        configValues.setReadTimeout(180000);
        return new DefaultWexyunApiClient(configValues);
    }
}
