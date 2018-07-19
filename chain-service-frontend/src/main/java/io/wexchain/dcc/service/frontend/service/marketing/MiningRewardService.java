package io.wexchain.dcc.service.frontend.service.marketing;

import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.BaseResponse;
import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.MiningRewardRecord;
import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;
import io.wexchain.dcc.service.frontend.model.request.QueryRedeemTokenQualificationRequest;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusRuleVo;
import io.wexchain.dcc.service.frontend.model.vo.EcoBonusVo;
import io.wexchain.dcc.service.frontend.model.vo.MiningRewardRecordVo;
import io.wexchain.dcc.service.frontend.model.vo.RedeemTokenQualificationVo;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

public interface MiningRewardService {

    /**
     * 查询奖励规则
     */
    List<EcoBonusRuleVo> queryRewardRule();


    /**
     * 查看挖矿贡献值
     */
    BigDecimal queryMiningContributionScore(String address);


    /**
     * 查询已获得贡献值列表
     */
    Pagination<MiningRewardRecordVo> queryMiningRewardRecordPage(PageParam pageParam, String address);

    Pagination<EcoBonusVo> queryBonus(PageParam pageParam, String address);

    BigDecimal getTotalEcoBonus(String address);

    /**
     * 签到
     */
    BaseResponse signIn(String address);
}
