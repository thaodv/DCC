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

    public static void main(String[] args) throws IOException {
        Web3j build = Web3j.build(new HttpService("http://10.65.209.49:6789"));
       /* p(build, "0xedec58f327173b7d2cae70daedce81f1895af0acd144aeccc7384261f2625ac0");
        p(build, "0x23811a7415f5e1dddbd7860f83005b2b99892e0a78b43a4d2ee4b57965e0e32d");
        p(build, "0x2bbc2f3851193b3a12233e5c972f09bdfabc7f9275a51e48388ddaad6a81e435");
        p(build, "0xe31359c9cd11f34541eda87b76676febde2c9337dc7825f532ada0b463ca3102");
        p(build, "0x11218d4dd6696ad3c07a3eecc31f3bc49abe5478d540ff52a62903674d72ed51");
        p(build, "0x98a9becb00c25fc1e436d71ac9b47fae2bc3b0795229c479c5c2992e4c904d9b");
*/
        try {
            Function function = new Function("getOwner",
                    Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
            String encodedFunction = FunctionEncoder.encode(function);

            EthCall response = build.ethCall(
                    Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000",
                            "0x032d2ae1712bb936ebdfe0e738317197b4a02735", encodedFunction),
                    DefaultBlockParameterName.LATEST)
                    .sendAsync().get();

            List<Type> someTypes = FunctionReturnDecoder.decode(
                    response.getValue(), function.getOutputParameters());

            System.out.println(someTypes.get(0));

        } catch (Exception e) {
            throw new ContextedRuntimeException(e);
        }

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
