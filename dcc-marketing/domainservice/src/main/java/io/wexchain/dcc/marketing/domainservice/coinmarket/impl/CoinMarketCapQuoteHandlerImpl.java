package io.wexchain.dcc.marketing.domainservice.coinmarket.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.godmonth.util.jackson.ObjectMapperFactoryBean;
import io.wexchain.dcc.marketing.api.model.Quote;
import io.wexchain.dcc.marketing.domainservice.coinmarket.QuoteAppender;
import io.wexchain.dcc.marketing.domainservice.coinmarket.QuoteHandler;
import io.wexchain.dcc.marketing.domainservice.coinmarket.model.CoinMarketConfig;
import io.wexchain.dcc.marketing.domainservice.coinmarket.model.Coinmarketcap;
import io.wexchain.dcc.marketing.domainservice.coinmarket.model.CoinmarketcapOrder;
import io.wexchain.dcc.marketing.domainservice.coinmarket.model.CoinmarketcapResponce;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * @author wuxinxin Created by wuxinxin on 2018/7/16.
 */
public class CoinMarketCapQuoteHandlerImpl implements QuoteHandler {

	private static final Logger logger = LoggerFactory.getLogger(CoinMarketCapQuoteHandlerImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapperFactoryBean objectMapper;

	private CoinMarketConfig coinMarketConfig;

	private QuoteAppender marketAppenders;

	@Override
	public void handle() {
		int total = 0;
		while (true) {
			// 处理一页数据
			Map<String, String> map = new HashMap<>();
			map.put("start", total + "");
			map.put("convert", "CNY");
			map.put("structure", "array");
			String forObject1 = restTemplate.getForObject(
					coinMarketConfig.getPath() + "?structure={structure}&convert={convert}&start={start}", String.class,
					map);
			try {
				CoinmarketcapResponce coinmarketcapResponce = objectMapper.getObject().readValue(forObject1,
						CoinmarketcapResponce.class);

				List<Quote> quoteList = handleHelp(coinmarketcapResponce);
				if (quoteList.size() != 0) {
					total += quoteList.size();
					marketAppenders.putMarketBySymbols(quoteList);
					logger.info("当前获取{}条数据", total);
				} else {
					logger.info("共获取{}条数据", total);
					break;
				}
			} catch (IOException e) {
				throw new ContextedRuntimeException(e);
			}
		}
	}

	// 对数据进行过滤
	private List<Quote> handleHelp(CoinmarketcapResponce coinmarketcapResponce) {
		List<Coinmarketcap> coinmarketcapList = coinmarketcapResponce.getData();
		List<Quote> quoteList = new ArrayList<Quote>();
		for (Coinmarketcap coinmarketcap : coinmarketcapList) {

			CoinmarketcapOrder cny = coinmarketcap.getQuotes().get("CNY");

			Quote quote = new Quote();
			quote.setFullName(coinmarketcap.getName());
			quote.setSymbol(coinmarketcap.getSymbol());

			quote.setQuoteTime(coinmarketcap.getLast_updated());
			quote.setPercentChange1H(cny.getPercent_change_1h());
			quote.setPercentChange24H(cny.getPercent_change_24h());
			quote.setPrice(cny.getPrice());

			quote.setVolume24H(cny.getVolume_24h());
			quote.setPercentChange7D(cny.getPercent_change_7d());

			// 计算24小时涨跌值
			if (quote.getPrice() != null && quote.getPercentChange24H() != null) {
				double priceChange24h = (quote.getPrice().doubleValue()
						* (quote.getPercentChange24H().doubleValue() / 100))
						/ (1 + (quote.getPercentChange24H().doubleValue() / 100));
				BigDecimal bigDecimal = new BigDecimal(priceChange24h);
				quote.setPriceChange24H(bigDecimal);
			}
			quoteList.add(quote);
		}
		return quoteList;
	}

	public void setCoinMarketConfig(CoinMarketConfig coinMarketConfig) {
		this.coinMarketConfig = coinMarketConfig;
	}

	public void setMarketAppenders(QuoteAppender marketAppenders) {
		this.marketAppenders = marketAppenders;
	}
}
