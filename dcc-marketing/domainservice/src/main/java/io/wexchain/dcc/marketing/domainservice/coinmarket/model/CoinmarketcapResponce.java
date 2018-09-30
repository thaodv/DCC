package io.wexchain.dcc.marketing.domainservice.coinmarket.model;

import java.util.List;

/**
 * Created by wuxinxin on 2018/7/16.
 */
public class CoinmarketcapResponce {
    private List<Coinmarketcap> data;

    public List<Coinmarketcap> getData() {
        return data;
    }

    public void setData(List<Coinmarketcap> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CoinmarketcapResponce{" +
                "data=" + data +
                '}';
    }
}
