package io.wexchain.dcc.marketing.domainservice.impl;


import io.wexchain.dcc.marketing.api.model.MarketDetailPo;
import io.wexchain.dcc.marketing.domainservice.CoinMarketService;
import io.wexchain.dcc.marketing.domainservice.coinmarket.MarketAppender;
import io.wexchain.dcc.marketing.domainservice.utils.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxinxin on 2018/7/16.
 */
@Service
public class CoinMarketServiceImpl implements CoinMarketService {

    private static final Logger logger = Logger.getLogger(CoinMarketServiceImpl.class);

    @Autowired
    @Qualifier("mapAppender")
    private MarketAppender mapAppender;

    @Autowired
    @Qualifier("redisAppender")
    private MarketAppender redisAppender;

    @Override
    public List<MarketDetailPo> getCoinMaketFromMaps(String market, String coinTypes){
        logger.info(mapAppender+" 获取多个币种行情==>"+market+coinTypes);
        List<MarketDetailPo> marketDetailPos=new ArrayList<MarketDetailPo>();
        //解析币种
        String[] stringArrayByString = StringUtil.getStringArrayByString(coinTypes);
        for(int i=0;i<stringArrayByString.length;i++){
            marketDetailPos.add(mapAppender.getMarketBySymbol(market+stringArrayByString[i]));
        }
        return marketDetailPos;
    }

    @Override
    public List<MarketDetailPo> getCoinMaketFromRedis(String market, String coinTypes){
        logger.info(redisAppender+" 获取多个币种行情==> "+market+coinTypes);
        List<MarketDetailPo> marketDetailPos=new ArrayList<MarketDetailPo>();
        //解析币种
        String[] stringArrayByString = StringUtil.getStringArrayByString(coinTypes);
        for(int i=0;i<stringArrayByString.length;i++){
            marketDetailPos.add(redisAppender.getMarketBySymbol(market+stringArrayByString[i]));
        }
        return marketDetailPos;
    }
}
