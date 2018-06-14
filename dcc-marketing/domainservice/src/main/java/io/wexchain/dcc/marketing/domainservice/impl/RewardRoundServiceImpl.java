package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.CustomeValidate;
import io.wexchain.dcc.marketing.api.constant.MarketingErrorCode;
import io.wexchain.dcc.marketing.api.constant.RewardRoundStatus;
import io.wexchain.dcc.marketing.api.model.EcoRewardStatisticsInfo;
import io.wexchain.dcc.marketing.domain.Activity;
import io.wexchain.dcc.marketing.domain.RewardRound;
import io.wexchain.dcc.marketing.domainservice.ActivityService;
import io.wexchain.dcc.marketing.domainservice.RewardRoundService;
import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.web3.Web3Function;
import io.wexchain.dcc.marketing.domainservice.processor.order.rewardround.RewardRoundInstruction;
import io.wexchain.dcc.marketing.repository.RewardActionRecordRepository;
import io.wexchain.dcc.marketing.repository.RewardDeliveryRepository;
import io.wexchain.dcc.marketing.repository.RewardRoundRepository;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * RewardRoundServiceImpl
 *
 * @author zhengpeng
 */
@Service
public class RewardRoundServiceImpl implements RewardRoundService {

    @Autowired
    private CahFunction cahFunction;

    @Autowired
    private Web3Function web3Function;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private RewardRoundRepository rewardRoundRepository;

    @Resource(name = "rewardRoundExecutor")
    private OrderExecutor<RewardRound, RewardRoundInstruction> rewardRoundExecutor;

    private static final String ECO_REWARD_ACTIVITY_CODE = "10003";

    @Autowired
    private RewardActionRecordRepository rewardActionRecordRepository;

    @Autowired
    private RewardDeliveryRepository rewardDeliveryRepository;

    @Override
    public RewardRound createRewardRound(Date bonusDay) {
        Date bd = new DateTime(bonusDay).withTimeAtStartOfDay().toDate();
        RewardRound rewardRound = findRewardRoundByBonusDay(bd).orElseGet(() -> {
            checkEcoRewardPoolBalance();
            RewardRound rr = new RewardRound();
            rr.setStartBlock(getStartBlockNumber(bd));
            rr.setEndBlock(getEndBlockNumber(bd));
            rr.setStatus(RewardRoundStatus.CREATED);
            rr.setActivity(activityService.getActivityByCode(ECO_REWARD_ACTIVITY_CODE));
            rr.setBonusDay(bd);
            return rewardRoundRepository.save(rr);
        });
        rewardRoundExecutor.executeAsync(rewardRound, null ,null);
        return rewardRound;
    }

    @Override
    public Optional<RewardRound> findRewardRoundByBonusDay(Date bonusDay) {
        return Optional.ofNullable(rewardRoundRepository.findByBonusDay(bonusDay));
    }

    @Override
    public EcoRewardStatisticsInfo getEcoRewardStatisticsInfo(String address) {
        Date yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
        EcoRewardStatisticsInfo info = new EcoRewardStatisticsInfo();
        info.setYesterdayAmount(BigDecimal.ZERO);
        info.setYesterdayScore(BigDecimal.ZERO);
        findRewardRoundByBonusDay(yesterday).ifPresent(rewardRound -> {
            CustomeValidate.isTrue(rewardRound.getStatus() == RewardRoundStatus.DELIVERED,
                    MarketingErrorCode.INVALID_STATUS.name(), "奖励发放中，请稍候重试");
            BigDecimal score = Optional.ofNullable(rewardActionRecordRepository.sumScoreByAddress(rewardRound.getId(), address))
                    .orElse(BigDecimal.ZERO);
            BigDecimal amount = Optional.ofNullable(rewardDeliveryRepository.sumAmountByAddress(rewardRound.getId(), address))
                    .orElse(BigDecimal.ZERO);
            info.setYesterdayScore(score);
            info.setYesterdayAmount(amount);
        });
        return info;
    }

    private Long getStartBlockNumber(Date bonusDay) {
        /*Page<RewardRound> lastRewardRound = rewardRoundRepository.findAll(
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "createdTime")));
        if (CollectionUtils.isNotEmpty(lastRewardRound.getContent())) {
            return lastRewardRound.getContent().get(0).getEndBlock();
        }*/
        return web3Function.getBlockNumberAfterTime(bonusDay);
    }

    private Long getEndBlockNumber(Date bonusDay) {
        DateTime day = new DateTime(bonusDay).plusDays(1);
        return web3Function.getBlockNumberAfterTime(day.toDate());
    }

    private void checkEcoRewardPoolBalance() {
        Activity activity = activityService.getActivityByCode(ECO_REWARD_ACTIVITY_CODE);
        BigInteger juzixDccBalance = cahFunction.getJuzixDccBalance(activity.getSupplierAddress());
    }
}
