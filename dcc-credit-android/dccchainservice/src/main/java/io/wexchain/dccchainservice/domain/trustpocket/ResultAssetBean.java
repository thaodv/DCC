package io.wexchain.dccchainservice.domain.trustpocket;

import java.math.BigDecimal;

/**
 * @author Created by Wangpeng on 2019/3/15 14:08.
 * usage:
 */
public class ResultAssetBean implements Comparable<ResultAssetBean> {
    
    public String url;
    public String code;
    public String name;
    public String value;
    public String value2;
    public int rank;
    
    public ResultAssetBean(String url, String code, String name, String value, String value2, int rank) {
        this.url = url;
        this.code = code;
        this.name = name;
        this.value = value;
        this.value2 = value2;
        this.rank = rank;
    }
    
    @Override
    public int compareTo(ResultAssetBean o) {
        
        if (new BigDecimal(this.value).subtract(new BigDecimal(o.value)) == BigDecimal.ZERO) {
            return this.rank - o.rank;
        } else {
            return new BigDecimal(o.value).compareTo(new BigDecimal(this.value));
        }
    }
}
