package io.wexchain.dcc.marketing.domainservice.coinmarket.impl;

import io.wexchain.dcc.marketing.domainservice.coinmarket.QuoteAppender;
import com.godmonth.util.jackson.ObjectMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.wexchain.dcc.marketing.api.model.Quote;

/**
 * @author wuxinxin
 */
public class RedisQuoteAppenderImpl implements QuoteAppender {

	@Autowired
	ObjectMapperFactoryBean objectMapper;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public List<Quote> getMarketBySymbols(List<String> quoteKeys) throws IOException {
		List<Quote> quoteList=new ArrayList<>();

		for(String quoteKey:quoteKeys){
			String s = stringRedisTemplate.opsForValue().get(quoteKey);
			if(s!=null){
				quoteList.add(objectMapper.getObject().readValue(s,Quote.class));
			}
		}
		return quoteList;
	}

	@Override
	public void putMarketBySymbols(List<Quote> quoteList) throws IOException{
		Map map=new HashMap<String,String>();
		for(Quote quote:quoteList){
			map.put(quote.getSymbol(),objectMapper.getObject().writeValueAsString(quote));
		}
		stringRedisTemplate.opsForValue().multiSet(map);
	}
}
