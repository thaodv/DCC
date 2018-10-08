package io.wexchain.dcc.marketing.domainservice.coinmarket.model;

/**
 * Created by wuxinxin on 2018/7/16.
 */
public class CoinMarketConfig {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CoinMarketConfig{" +
                "path='" + path + '\'' +
                '}';
    }
}
