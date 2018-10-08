package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.executor.intf.OrderExecutor;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import io.wexchain.cryptoasset.loan.api.constant.AuditingOrderStatus;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.RebateOrder;
import io.wexchain.cryptoasset.loan.repository.AuditingOrderRepository;
import io.wexchain.cryptoasset.loan.service.RebateService;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class PassAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

	{
		availableStatus = LoanOrderStatus.AUDITING;
	}

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChainOrderService chainOrderService;

    @Autowired
    private AuditingOrderRepository auditingOrderRepository;

    @Autowired
    private RebateService rebateService;

    @Resource(name = "auditingOrderExecutor")
    private OrderExecutor<AuditingOrder, Void> auditingOrderExecutor;

    @Override
    public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(
            LoanOrder loanOrder, LoanOrderInstruction instruction, Object message) {

        logger.info("Loan order check audit result:{} --> start", loanOrder.getId());

        AuditingOrder auditingOrder = prepareAuditingOrder(loanOrder);
        logger.info("Loan order check audit result:{} --> prepare auditing order:{}, status:{}",
                loanOrder.getId(), auditingOrder.getId(), auditingOrder.getStatus());

        AuditingOrder advancedAuditingOrder = auditingOrderExecutor.execute(
                auditingOrder, null, null).getModel();
        logger.info("Loan order check audit result:{} --> advance auditing order:{}, status:{}",
                loanOrder.getId(), advancedAuditingOrder.getId(), advancedAuditingOrder.getStatus());

        if (advancedAuditingOrder.getStatus() == AuditingOrderStatus.APPROVED) {
            chainOrderService.approve(loanOrder.getChainOrderId());
        }

        if (advancedAuditingOrder.getStatus() == AuditingOrderStatus.REJECTED) {
            chainOrderService.reject(loanOrder.getChainOrderId());
        }

        io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder =
                chainOrderService.getLoanOrder(loanOrder.getChainOrderId());
        if (chainLoanOrder.getStatus() == OrderStatus.APPROVED) {
            return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.APPROVE, innerLoanOrder -> {
                innerLoanOrder.setApplyId(advancedAuditingOrder.getApplyId());
            }));
        }
        if (chainLoanOrder.getStatus() == OrderStatus.REJECTED) {
            RebateOrder rebateOrder = rebateService.rejectOrDeliverFail(loanOrder.getId());
            if (rebateOrder != null) {
                return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.REJECT, innerLoanOrder -> {
                    if (StringUtils.isNotEmpty(advancedAuditingOrder.getApplyId())) {
                        innerLoanOrder.setApplyId(advancedAuditingOrder.getApplyId());
                    }
                }));
            }
        }

        return null;
    }

    private AuditingOrder prepareAuditingOrder(LoanOrder loanOrder) {
        return auditingOrderRepository.findById(loanOrder.getId()).orElseGet(() -> {
            AuditingOrder auditingOrder = new AuditingOrder();
            auditingOrder.setId(loanOrder.getId());
            auditingOrder.setStatus(AuditingOrderStatus.CREATED);
            return auditingOrderRepository.save(auditingOrder);
        });
    }
}
