package io.wexchain.dcc.marketing.domainservice.coinmarket.impl;
import com.alibaba.fastjson.JSONObject;
import io.wexchain.dcc.marketing.api.model.*;
import io.wexchain.dcc.marketing.domainservice.coinmarket.MarketAppender;
import io.wexchain.dcc.marketing.domainservice.coinmarket.MarketHandle;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * Created by wuxinxin on 2018/7/16.
 */
public class CoinMarketCapHandleImpl implements MarketHandle {

    private static final String PREFIX="coinMarketCap";

    private static final Logger logger = Logger.getLogger(CoinMarketCapHandleImpl.class);

    private RestTemplate restTemplate;

    private CoinMarketConfig coinMarketConfig;

    private List<MarketAppender> marketAppenders;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setCoinMarketConfig(CoinMarketConfig coinMarketConfig) {
        this.coinMarketConfig = coinMarketConfig;
    }

    public void setMarketAppenders(List<MarketAppender> marketAppenders) {
        this.marketAppenders = marketAppenders;
    }

    @Override
    public void handle() {
        logger.info("请求接口参数:"+coinMarketConfig);
        CoinmarketcapResponce coinmarketcapResponce=null;
        for(int i=0;i<coinMarketConfig.getMaxPage();i++){
            try {
                //处理一页数据
                Map<String, String> map = new HashMap<String, String>();
                map.put("start", (coinMarketConfig.getSize() * i + 1) + "");
                map.put("convert", "CNY");
                map.put("structure", "array");
                String forObject1 = restTemplate.getForObject(coinMarketConfig.getPath() + "?structure={structure}&convert={convert}&start={start}", String.class, map);

                coinmarketcapResponce = JSONObject.parseObject(forObject1, CoinmarketcapResponce.class);
                logger.info("抓取第" + i + "页:");
                List<MarketDetailPo> marketDetailPos = handleHelp(coinmarketcapResponce);
                store(marketDetailPos);
            }catch (Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    //对数据进行过滤
    private  List<MarketDetailPo> handleHelp(CoinmarketcapResponce coinmarketcapResponce){
        List<Coinmarketcap> data = coinmarketcapResponce.getData();
        List<MarketDetailPo> marketDetailPoList=new ArrayList<MarketDetailPo>();
        for(int i=0;i<data.size();i++){
            Coinmarketcap coinmarketcap = data.get(i);
            CoinmarketcapOrder cny = coinmarketcap.getQuotes().getCNY();

            MarketDetailPo marketDetailPo=new MarketDetailPo();
            marketDetailPo.setFullName(coinmarketcap.getName());
            marketDetailPo.setSymbol(coinmarketcap.getSymbol());

            marketDetailPo.setTimeStamp(coinmarketcap.getLast_updated());
            marketDetailPo.setPercent_change_1h(cny.getPercent_change_1h());
            marketDetailPo.setPercent_change_24h(cny.getPercent_change_24h());
            marketDetailPo.setPrice(cny.getPrice());
            //logger.info(marketDetailPo.getFullName()+"\t"+cny.getVolume_24h());
            marketDetailPo.setVolume_24(cny.getVolume_24h());
            marketDetailPo.setPercent_change_7d(cny.getPercent_change_7d());

            //计算24小时涨跌值
            if(marketDetailPo.getPrice()!=null && marketDetailPo.getPercent_change_24h()!=null) {
                double price_change_24h = (marketDetailPo.getPrice().doubleValue() * (marketDetailPo.getPercent_change_24h().doubleValue() / 100)) / (1 + (marketDetailPo.getPercent_change_24h().doubleValue() / 100));
                BigDecimal bigDecimal = new BigDecimal(price_change_24h);
                marketDetailPo.setPrice_change_24(bigDecimal);
            }

            marketDetailPoList.add(marketDetailPo);
            //logger.info("币种:"+marketDetailPo.getFullName()+"\t"+marketDetailPo.getSymbol()+"\t"+marketDetailPo.getPrice());
        }
        return marketDetailPoList;
    }

    //将获取到的数据进行分发（直接到内存MAP中，或者到redis）
    private void store(List<MarketDetailPo> marketDetailPos){
        for(int i=0;i<marketAppenders.size();i++) {
            marketAppenders.get(i).putMarketBySymbol(PREFIX, marketDetailPos);
        }
    }
}
