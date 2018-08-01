package io.wexchain.dcc.marketing.domainservice.coinmarket.impl;

import io.wexchain.dcc.marketing.api.model.MarketDetailPo;
import io.wexchain.dcc.marketing.domainservice.coinmarket.MarketAppender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将行情数据缓存在内存中
 * Created by wuxinxin on 2018/7/16.
 */
public class MapAppenderImpl implements MarketAppender {

    private Map<String, MarketDetailPo> marketDetailPoMaps=new HashMap<String, MarketDetailPo>();

    @Override
    public void putMarketBySymbol(String sourceCode,List<MarketDetailPo> marketDetailPoList) {
        int size = marketDetailPoList.size();
        for(int i=0;i<size;i++){
            MarketDetailPo marketDetailPo = marketDetailPoList.get(i);
            marketDetailPoMaps.put(sourceCode+marketDetailPo.getSymbol(),marketDetailPo);
        }
    }

    @Override
    public MarketDetailPo getMarketBySymbol(String symbol) {
        if(marketDetailPoMaps.containsKey(symbol)){
            return marketDetailPoMaps.get(symbol);
        }
        return new MarketDetailPo();
    }
}
