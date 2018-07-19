package io.wexchain.cryptoasset.loan.service.processor.order.rebate.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.enums.DurationType;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.BatchTransferOrder;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.constant.RebateOrderStatus;
import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.*;
import io.wexchain.cryptoasset.loan.repository.RebateOrderRepository;
import io.wexchain.cryptoasset.loan.service.RebateService;
import io.wexchain.cryptoasset.loan.service.constant.CommandName;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.command.CommandIndex;
import io.wexchain.cryptoasset.loan.service.function.command.RetryableCommandHelper;
import io.wexchain.cryptoasset.loan.service.function.command.RetryableCommandTemplate;
import io.wexchain.cryptoasset.loan.service.function.command.UnretryableCommandFunction;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2ApplyAddRequest;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.cryptoasset.loan.service.processor.order.rebate.RebateOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.rebate.RebateOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RebateAdvancer extends AbstractAdvancer<RebateOrder, RebateOrderInstruction, RebateOrderTrigger> {


    private Logger logger = LoggerFactory.getLogger(RebateAdvancer.class);

	{
		availableStatus = RebateOrderStatus.CREATED;
	}

    @Autowired
    private RebateService rebateService;

    @Autowired
    private CahFunction cahFunction;

    @Autowired
    private RetryableCommandTemplate retryableCommandTemplate;

    @Autowired
    private RebateOrderRepository rebateOrderRepository;

    @Override
    public AdvancedResult<RebateOrder, RebateOrderTrigger> advance(RebateOrder rebateOrder, RebateOrderInstruction rebateOrderInstruction, Object o) {
        List<RebateItem> rebateItems = rebateService.getRebateItemsAndAmountNotZero(rebateOrder.getId());
        if(CollectionUtils.isNotEmpty(rebateItems)){
            RetryableCommand collectCommand = executeRebate(rebateOrder, rebateItems);

            if (RetryableCommandHelper.isSuccess(collectCommand)) {
                return new AdvancedResult<>(new TriggerBehavior<>(RebateOrderTrigger.SUCCESS));
            }
        }else {
            return new AdvancedResult<>(new TriggerBehavior<>(RebateOrderTrigger.SUCCESS));
        }
        return null;
    }

    private RetryableCommand executeRebate(RebateOrder rebateOrder, List<RebateItem> rebateItems) {
        // 将数字资产转移到归集账户
        CommandIndex commandIndex = new CommandIndex(RebateOrder.TYPE_REF, rebateOrder.getId(), CommandName.CMD_REBATE);
        return retryableCommandTemplate.execute(commandIndex,
                ci -> Validate.notNull(rebateOrderRepository.lockById(ci.getParentId())), null,
                command -> {
                    if (RetryableCommandHelper.isCreated(command)) {
                        BatchTransferOrder batchTransferOrder = cahFunction.rebate(
                                String.valueOf(command.getId()),
                                rebateItems);
                        if (batchTransferOrder.getStatus() == TransferOrderStatus.SUCCESS) {
                            return GeneralCommandStatus.SUCCESS.name();
                        } else if (batchTransferOrder.getStatus() == TransferOrderStatus.FAILURE) {
                            return GeneralCommandStatus.FAILURE.name();
                        }
                    }
                    return command.getStatus();
                });
    }
}
