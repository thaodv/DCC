package io.wexchain.dcc.marketing.domainservice;

import java.math.BigDecimal;

/**
 * MiningAccountBalanceService
 *
 * @author fu qiliang
 */
public interface MiningContributionScoreService {
    /** 获取挖矿贡献值 */
    BigDecimal queryMiningContributionScore(String address);

}
