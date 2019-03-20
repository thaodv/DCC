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
    public String value = "0.0000";
    public String value2 = "--";
    public BigDecimal value3 = BigDecimal.ZERO;
    public int rank;
    
    /*public ResultAssetBean(String url, String code, String name, String value, String value2, int rank) {
        this.url = url;
        this.code = code;
        this.name = name;
        this.value = value;
        this.value2 = value2;
        this.rank = rank;
    }*/
    
    @Override
    public int compareTo(ResultAssetBean o) {
        
        if (this.value3.subtract(o.value3) == BigDecimal.ZERO) {
            return this.rank - o.rank;
        } else {
            return o.value3.compareTo(this.value3);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ResultAssetBean resultAssetBean = (ResultAssetBean) obj;
        return this.code.equals(resultAssetBean.code) && this.url.equals(resultAssetBean.url);
    }
}
