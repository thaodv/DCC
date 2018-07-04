package io.wexchain.dcc.marketing.ext.service;

import com.wexmarket.topia.commons.basic.rpc.utils.ListResultResponseUtils;
import com.wexmarket.topia.commons.basic.rpc.utils.ResultResponseUtils;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.rpc.ListResultResponse;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.dcc.marketing.api.facade.MiningRewardFacade;
import io.wexchain.dcc.marketing.api.model.EcoRewardRule;
import io.wexchain.dcc.marketing.api.model.MiningRewardRecord;
import io.wexchain.dcc.marketing.api.model.request.QueryMiningRewardRecordPageRequest;
import io.wexchain.dcc.marketing.api.model.request.QueryRewardRuleRequest;
import io.wexchain.dcc.marketing.domainservice.MiningContributionScoreService;
import io.wexchain.dcc.marketing.domainservice.MiningRewardRecordService;
import io.wexchain.dcc.marketing.ext.service.helper.EcoRewardRuleResponseHelper;
import io.wexchain.dcc.marketing.ext.service.helper.MiningRewardRecordResponseHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

/**
 * MiningRewardFacadeImpl
 *
 * @author fu qiliang
 */
@Component("miningRewardFacade")
@Validated
public class MiningRewardFacadeImpl implements MiningRewardFacade {

	@Autowired
	private MiningContributionScoreService miningContributionScoreService;

	@Autowired
	private MiningRewardRecordService miningRewardRecordService;

	@Autowired
	private MiningRewardRecordResponseHelper miningRewardRecordResponseHelper;

	@Autowired
	private EcoRewardRuleResponseHelper ecoRewardRuleResponseHelper;

	@Override
	public ListResultResponse<EcoRewardRule> queryRewardRule(QueryRewardRuleRequest request) {
		try {
			List<io.wexchain.dcc.marketing.domain.EcoRewardRule> result = miningRewardRecordService.queryRewardRule(request);
			return ecoRewardRuleResponseHelper.returnListSuccess(result);
		} catch (Exception e) {
			return ListResultResponseUtils.exceptionListResultResponse(e);
		}
	}

	@Override
	public ResultResponse<BigDecimal> queryMiningContributionScore(String address) {
		try {
			BigDecimal accountBalance = miningContributionScoreService.queryMiningContributionScore(address);
			return ResultResponseUtils.successResultResponse(accountBalance);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

	@Override
	public ResultResponse<Pagination<MiningRewardRecord>> queryMiningRewardRecordPage(
			QueryMiningRewardRecordPageRequest request) {
		try {
			Page<io.wexchain.dcc.marketing.domain.MiningRewardRecord> pageResult = miningRewardRecordService
					.queryMiningRewardRecordPage(request);
			return miningRewardRecordResponseHelper.returnPageSuccess(pageResult);
		} catch (Exception e) {
			return ResultResponseUtils.exceptionResultResponse(e);
		}
	}

}
