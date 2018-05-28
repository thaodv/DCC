package io.wexchain.cryptoasset.loan.ext.integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weihui.finance.contract.api.request.GeneratPDFFileRequest;
import com.weihui.finance.contract.api.response.GeneratPDFFileResponse;
import com.weihui.finance.contract.client.util.ContractClient;
import com.wexyun.open.api.client.ConfigValues;
import com.wexyun.open.api.client.DefaultWexyunApiClient;
import com.wexyun.open.api.client.WexyunApiClient;
import com.wexyun.open.api.domain.regular.loan.RegularPrepaymentBill;
import com.wexyun.open.api.enums.log.LogLevel;
import com.wexyun.open.api.exception.WexyunClientException;
import com.wexyun.open.api.request.loan.RegularPrepaymentBillGetRequest;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.loan.common.exception.RpcException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
