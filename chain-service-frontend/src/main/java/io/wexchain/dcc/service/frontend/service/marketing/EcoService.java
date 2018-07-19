package io.wexchain.dcc.service.frontend.service.marketing;

import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import io.wexchain.dcc.marketing.api.model.RewardRound;
import io.wexchain.dcc.marketing.api.model.request.CreateRewardRoundRequest;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.YesterdayEcoBonusVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface EcoService {

    Pagination<EcoBonusVo> queryBonus(PageParam pageParam,String address);

    BigDecimal getTotalEcoBonus(String address);

    YesterdayEcoBonusVo getYesterdayBonus(String address);

    List<EcoBonusRuleVo> queryBonusRule();

    /**
     * 创建奖励轮次
     */
    RewardRound createRewardRound(Date bonusDay);
}
