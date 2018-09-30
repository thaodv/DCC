package io.wexchain.dcc.marketing.domainservice.coinmarket.model;

import java.util.Map;

/**
 * Created by wuxinxin on 2018/7/16.
 */
public class Coinmarketcap {
    private String name;
    private String symbol;
    Map<String,CoinmarketcapOrder> quotes;
    private long last_updated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Map<String, CoinmarketcapOrder> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, CoinmarketcapOrder> quotes) {
        this.quotes = quotes;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    @Override
    public String toString() {
        return "Coinmarketcap{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", quotes=" + quotes +
                ", last_updated=" + last_updated +
                '}';
    }
}
