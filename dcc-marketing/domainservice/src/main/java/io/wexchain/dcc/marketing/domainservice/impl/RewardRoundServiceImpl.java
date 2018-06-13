package io.wexchain.dcc.marketing.domainservice.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.CustomeValidate;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
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
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

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
    public RewardRound createRewardRound() {
        checkEcoRewardPoolBalance();

        RewardRound rewardRound = new RewardRound();
        rewardRound.setStartBlock(getStartBlockNumber());
        rewardRound.setEndBlock(getEndBlockNumber());
        rewardRound.setStatus(RewardRoundStatus.CREATED);
        rewardRound.setActivity(activityService.getActivityByCode(ECO_REWARD_ACTIVITY_CODE));
        rewardRound.setBonusDay(DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate());
        rewardRound = rewardRoundRepository.save(rewardRound);

        rewardRoundExecutor.executeAsync(rewardRound, null ,null);
        return rewardRound;
    }

    @Override
    public RewardRound findRewardRoundByBonusDay(Date bonusDay) {
        return ErrorCodeValidate.notNull(
                rewardRoundRepository.findByBonusDay(bonusDay),
                MarketingErrorCode.REWARD_ROUND_NOT_FOUND);
    }

    @Override
    public EcoRewardStatisticsInfo getEcoRewardStatisticsInfo(String address) {
        Date yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
        RewardRound rewardRound = findRewardRoundByBonusDay(yesterday);

        CustomeValidate.isTrue(rewardRound.getStatus() == RewardRoundStatus.DELIVERED,
                MarketingErrorCode.INVALID_STATUS.name(), "奖励发放中，请稍候重试");

        EcoRewardStatisticsInfo info = new EcoRewardStatisticsInfo();
        info.setYesterdayScore(rewardActionRecordRepository.sumScore(rewardRound.getId()));
        info.setYesterdayAmount(rewardDeliveryRepository.sumAmountByRewardRoundId(rewardRound.getId()));
        return info;
    }

    private Long getStartBlockNumber() {
        Page<RewardRound> lastRewardRound = rewardRoundRepository.findAll(
                PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "createdTime")));
        if (CollectionUtils.isNotEmpty(lastRewardRound.getContent())) {
            return lastRewardRound.getContent().get(0).getEndBlock();
        }
        DateTime day = DateTime.now().minusDays(1).withTimeAtStartOfDay();
        return web3Function.getBlockNumberAfterTime(day.toDate());
    }

    private Long getEndBlockNumber() {
        DateTime day = DateTime.now().withTimeAtStartOfDay();
        return web3Function.getBlockNumberAfterTime(day.toDate());
    }

    private void checkEcoRewardPoolBalance() {
        Activity activity = activityService.getActivityByCode(ECO_REWARD_ACTIVITY_CODE);
        BigInteger juzixDccBalance = cahFunction.getJuzixDccBalance(activity.getSupplierAddress());
    }

    public static void main(String[] args) throws IOException {
        Web3j build = Web3j.build(new HttpService("http://10.65.209.49:6789"));
        p(build, "0xedec58f327173b7d2cae70daedce81f1895af0acd144aeccc7384261f2625ac0");
        p(build, "0x23811a7415f5e1dddbd7860f83005b2b99892e0a78b43a4d2ee4b57965e0e32d");
        p(build, "0x2bbc2f3851193b3a12233e5c972f09bdfabc7f9275a51e48388ddaad6a81e435");
        p(build, "0xe31359c9cd11f34541eda87b76676febde2c9337dc7825f532ada0b463ca3102");
        p(build, "0x11218d4dd6696ad3c07a3eecc31f3bc49abe5478d540ff52a62903674d72ed51");
        p(build, "0x98a9becb00c25fc1e436d71ac9b47fae2bc3b0795229c479c5c2992e4c904d9b");
    }

    public static void p(Web3j web3j, String address) throws IOException {
        EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(address).send();
        TransactionReceipt result = receipt.getResult();
        List<Log> logs = result.getLogs();
        Log log = logs.get(0);
        System.out.println("address: " + address);
        System.out.println("data: " + log.getData());
        System.out.println("topic: " + log.getTopics());
        System.out.println("----------------------------------------------");
    }


}