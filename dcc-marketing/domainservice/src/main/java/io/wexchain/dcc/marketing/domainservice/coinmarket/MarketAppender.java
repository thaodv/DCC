package io.wexchain.dcc.marketing.domainservice.coinmarket;


import io.wexchain.dcc.marketing.api.model.MarketDetailPo;

import java.util.List;

/**
 * Created by wuxinxin on 2018/7/16.
 */
public interface  MarketAppender {
    MarketDetailPo getMarketBySymbol(String symbol);
    void putMarketBySymbol(String sourceCode, List<MarketDetailPo> marketDetailPoList);
}
