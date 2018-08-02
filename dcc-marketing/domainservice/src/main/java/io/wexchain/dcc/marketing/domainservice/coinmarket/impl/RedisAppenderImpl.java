package io.wexchain.dcc.marketing.domainservice.coinmarket.impl;


import io.wexchain.dcc.marketing.api.model.MarketDetailPo;
import io.wexchain.dcc.marketing.domainservice.coinmarket.MarketAppender;
import io.wexchain.dcc.marketing.domainservice.utils.PojoToJsonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by wuxinxin on 2018/7/20.
 */
public class RedisAppenderImpl implements MarketAppender {

    private static final Logger logger = Logger.getLogger(CoinMarketCapHandleImpl.class);
    private static final String redisKeyName="coinmarket";

    private String hostName;

    @Value("${redis.hostName}")
    public void setHostName(String hostName) {
        logger.info("${redis.hostName}==>"+hostName);
        this.hostName = hostName;
    }

    @Override
    public MarketDetailPo getMarketBySymbol(String symbol) {
        Jedis connect=new Jedis(hostName);
        String hget = connect.hget(redisKeyName, symbol);
        if(hget!=null){
            return PojoToJsonUtil.JsonToMarketDetailPo(hget);
        }
        return new MarketDetailPo();
    }

    @Override
    public void putMarketBySymbol(String sourceCode, List<MarketDetailPo> marketDetailPoList) {
        Jedis connect=new Jedis(hostName);
        for (int i=0;i<marketDetailPoList.size();i++){
            MarketDetailPo marketDetailPo = marketDetailPoList.get(i);
            connect.hset(redisKeyName,sourceCode+marketDetailPo.getSymbol(),PojoToJsonUtil.MarketDetailPoToJson(marketDetailPo));
        }
    }
}
