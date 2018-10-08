package io.wexchain.dcc.marketing.domainservice.coinmarket;

import java.io.IOException;
import java.util.List;

import io.wexchain.dcc.marketing.api.model.Quote;

/**
 * Created by wuxinxin on 2018/7/16.
 */
public interface QuoteAppender {
	List<Quote> getMarketBySymbols(List<String> quoteKeys) throws IOException;

	void putMarketBySymbols(List<Quote> quoteList) throws IOException;
}
