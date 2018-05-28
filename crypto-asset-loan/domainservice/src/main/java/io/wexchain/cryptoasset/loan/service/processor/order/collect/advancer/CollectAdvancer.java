package io.wexchain.cryptoasset.loan.service.processor.order.collect.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.loan.api.constant.CollectOrderStatus;
import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.CollectOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.RetryableCommand;
import io.wexchain.cryptoasset.loan.repository.CollectOrderRepository;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import io.wexchain.cryptoasset.loan.service.constant.CommandName;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.function.command.CommandIndex;
import io.wexchain.cryptoasset.loan.service.function.command.RetryableCommandHelper;
import io.wexchain.cryptoasset.loan.service.function.command.RetryableCommandTemplate;
import io.wexchain.cryptoasset.loan.service.processor.order.collect.CollectOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.collect.CollectOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import io.wexchain.dcc.loan.sdk.service.LoanService;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CollectAdvancer extends AbstractAdvancer<CollectOrder, CollectOrderInstruction, CollectOrderTrigger> {

	{
		availableStatus = CollectOrderStatus.FUELED;
	}

	@Autowired
	private LoanService loanService;

    @Autowired
    private CryptoAssetLoanService cryptoAssetLoanService;

    @Autowired
    private CahFunction cahFunction;

    @Autowired
    private RetryableCommandTemplate retryableCommandTemplate;

    @Autowired
    private CollectOrderRepository collectOrderRepository;

    @Override
    public AdvancedResult<CollectOrder, CollectOrderTrigger> advance(
            CollectOrder collectOrder, CollectOrderInstruction instruction, Object message) {
        try {
            LoanOrder loanOrder = cryptoAssetLoanService.getLoanOrder(collectOrder.getId());
            io.wexchain.dcc.loan.sdk.contract.LoanOrder chainOrder = loanService.getOrder(loanOrder.getChainOrderId());
            if (chainOrder.getStatus() == OrderStatus.REPAID) {

                RetryableCommand collectCommand = executeCollect(collectOrder, loanOrder);

                if (RetryableCommandHelper.isSuccess(collectCommand)) {
                    return new AdvancedResult<>(new TriggerBehavior<>(CollectOrderTrigger.COLLECT, co -> {
                        co.setCollectAmount(new BigDecimal(collectCommand.getMemo()));
                    }));
                }
            }
        } catch (IOException e) {
            throw new ContextedRuntimeException(e);
        }
        return null;
    }

    private RetryableCommand executeCollect(CollectOrder collectOrder, LoanOrder loanOrder) {
        // 将数字资产转移到归集账户
        CommandIndex commandIndex = new CommandIndex(CollectOrder.TYPE_REF, collectOrder.getId(), CommandName.CMD_COLLECT);
        return retryableCommandTemplate.execute(commandIndex,
                ci -> Validate.notNull(collectOrderRepository.lockById(ci.getParentId())), null,
                command -> {
                    if (RetryableCommandHelper.isCreated(command)) {
                        TransferOrder transferOrder = cahFunction.collect(
                                String.valueOf(command.getId()),
                                collectOrder.getRepayAmount(),
                                loanOrder.getRepayAddress(),
                                loanOrder.getAssetCode());
                        if (transferOrder.getStatus() == TransferOrderStatus.SUCCESS) {
                            command.setMemo(AmountScaleUtil.cah2Cal(transferOrder.getAmount()).toString());
                            return GeneralCommandStatus.SUCCESS.name();
                        } else if (transferOrder.getStatus() == TransferOrderStatus.FAILURE) {
                            return GeneralCommandStatus.FAILURE.name();
                        }
                    }
                    return command.getStatus();
                });
    }
}
