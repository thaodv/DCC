package io.wexchain.dccchainservice.domain.trustpocket;

/**
 * @author Created by Wangpeng on 2019/3/15 14:08.
 * usage:
 */
public class ResultAssetBean {
    
    public String url;
    public String code;
    public String name;
    public String value;
    public String value2;
    
    public ResultAssetBean(String url, String code, String name, String value, String value2) {
        this.url = url;
        this.code = code;
        this.name = name;
        this.value = value;
        this.value2 = value2;
    }
}
