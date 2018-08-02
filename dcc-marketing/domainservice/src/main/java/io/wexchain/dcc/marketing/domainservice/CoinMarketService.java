package io.wexchain.dcc.marketing.domainservice;

import io.wexchain.dcc.marketing.api.model.MarketDetailPo;

import java.util.List;

/**
 * Created by wuxinxin on 2018/7/26.
 */

public interface CoinMarketService {
     List<MarketDetailPo> getCoinMaketFromMaps(String market, String coinTypes);
     List<MarketDetailPo> getCoinMaketFromRedis(String market,String coinTypes);
}
