package io.wexchain.cryptoasset.loan.service.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.basic.patroller.Patroller;
import com.wexyun.open.api.domain.member.Member;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.api.constant.RebateOrderStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.RebateItem;
import io.wexchain.cryptoasset.loan.domain.RebateOrder;
import io.wexchain.cryptoasset.loan.repository.LoanOrderRepository;
import io.wexchain.cryptoasset.loan.repository.RebateItemRepository;
import io.wexchain.cryptoasset.loan.repository.RebateOrderRepository;
import io.wexchain.cryptoasset.loan.service.RebateService;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.processor.order.rebate.RebateOrderInstruction;
import jodd.bean.BeanCopy;
import jodd.bean.BeanUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class RebateServiceImpl implements RebateService, Patroller {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${fail.capital.rebate.percent}")
	private BigDecimal FAIL_CAPITAL_REBATE_PERCENT;

	@Value("${success_capital_rebate_percent}")
	private BigDecimal SUCCESS_CAPITAL_REBATE_PERCENT;

	@Value("${capital.address}")
	private String capitalAddress;

	@Autowired
	private RebateOrderRepository rebateOrderRepository;

	@Autowired
	private RebateItemRepository rebateItemRepository;

	@Autowired
	private LoanOrderRepository loanOrderRepository;

	@Resource(name = "rebateOrderExecutor")
	private OrderExecutor<RebateOrder, RebateOrderInstruction> rebateOrderExecutor;

	@Autowired
	private ChainOrderService chainOrderService;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Override
	public RebateOrder rejectOrDeliverFail(Long id) {
		LoanOrder loanOrder = loanOrderRepository.findById(id).get();
		// 查询链上订单
		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder = chainOrderService
				.getLoanOrder(loanOrder.getChainOrderId());
		BigDecimal rebateTotalFee = chainOrderService.getRebateTotalFee(loanOrder.getChainOrderId());

		RebateOrder rebateOrder = getRebateOrderByIdNullable(id).orElseGet(() -> {
			List<RebateItem> rebateItems = new ArrayList<>();

			RebateItem borrowerRebateItem = new RebateItem();
			borrowerRebateItem.setAddress(chainLoanOrder.getBorrower());
			borrowerRebateItem
					.setAmount(rebateTotalFee.multiply(FAIL_CAPITAL_REBATE_PERCENT).setScale(4, RoundingMode.DOWN));
			borrowerRebateItem.setRebateOrderId(id);

			RebateItem capitalRebateItem = new RebateItem();
			capitalRebateItem.setAddress(capitalAddress);
			capitalRebateItem.setRebateOrderId(id);
			capitalRebateItem
					.setAmount(rebateTotalFee.subtract(borrowerRebateItem.getAmount()).setScale(4, RoundingMode.DOWN));

			rebateItems.add(borrowerRebateItem);
			rebateItems.add(capitalRebateItem);

			// 创建新订单
			RebateOrder order = new RebateOrder();
			order.setId(id);
			order.setStatus(RebateOrderStatus.CREATED);

			return transactionTemplate.execute(arg0 -> {
				rebateItemRepository.saveAll(rebateItems);
				return rebateOrderRepository.save(order);
			});

		});

		rebateOrderExecutor.executeAsync(rebateOrder, null, null);

		return rebateOrder;
	}

	@Override
	public RebateOrder deliverSuccess(Long id) {
		LoanOrder loanOrder = loanOrderRepository.findById(id).get();
		// 查询链上订单
		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder = chainOrderService
				.getLoanOrder(loanOrder.getChainOrderId());
		BigDecimal rebateTotalFee = chainOrderService.getRebateTotalFee(loanOrder.getChainOrderId());
		logger.debug("rebateTotalFee:{}", rebateTotalFee);
		RebateOrder rebateOrder = getRebateOrderByIdNullable(id).orElseGet(() -> {
			String inviteAddress = null;
			Member member = wexyunLoanClient.getMemberInfoById(loanOrder.getMemberId());
			if (member != null && member.getInviteMemberId() != null) {
				inviteAddress = wexyunLoanClient.getAddressById(member.getInviteMemberId());
			}
			logger.debug("member:{}", member.getMemberId());
			List<RebateItem> rebateItems = new ArrayList<>();

			RebateItem borrowerRebateItem = new RebateItem();
			borrowerRebateItem.setAddress(chainLoanOrder.getBorrower());
			borrowerRebateItem
					.setAmount(rebateTotalFee.multiply(SUCCESS_CAPITAL_REBATE_PERCENT).setScale(4, RoundingMode.DOWN));
			borrowerRebateItem.setRebateOrderId(id);

			RebateItem capitalRebateItem = new RebateItem();
			capitalRebateItem.setRebateOrderId(id);
			capitalRebateItem
					.setAmount(rebateTotalFee.subtract(borrowerRebateItem.getAmount()).setScale(4, RoundingMode.DOWN));

			if (inviteAddress != null) {
				capitalRebateItem.setAddress(inviteAddress);
			} else {
				capitalRebateItem.setAddress(capitalAddress);
			}
			rebateItems.add(borrowerRebateItem);
			rebateItems.add(capitalRebateItem);

			// 创建新订单
			RebateOrder order = new RebateOrder();
			order.setId(id);
			order.setStatus(RebateOrderStatus.CREATED);
			logger.debug("before save order:{}", order.getId());
			return transactionTemplate.execute(arg0 -> {
				rebateItemRepository.saveAll(rebateItems);
				return rebateOrderRepository.save(order);
			});

		});
		logger.debug("rebateOrder:{}", rebateOrder.getId());
		rebateOrderExecutor.executeAsync(rebateOrder, null, null);
		return rebateOrder;
	}

	@Override
	public Optional<RebateOrder> getRebateOrderByIdNullable(Long id) {
		Optional<RebateOrder> rebateOrder = rebateOrderRepository.findById(id);
		return rebateOrder;
	}

	@Override
	public RebateOrder getRebateOrder(Long orderId) {
		return ErrorCodeValidate.notNull(rebateOrderRepository.findById(orderId).orElse(null),
				CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public List<RebateItem> getRebateItems(Long orderId) {
		return ErrorCodeValidate.notNull(rebateItemRepository.findByRebateOrderId(orderId),
				CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public List<RebateItem> getRebateItemsAndAmountNotZero(Long orderId) {
		List<RebateItem> rebateItems = getRebateItems(orderId);
		List<RebateItem> result = new ArrayList<>();
		rebateItems.forEach(rebateItem -> {
			if (rebateItem.getAmount().compareTo(BigDecimal.ZERO) == 1) {
				result.add(rebateItem);
			}
		});
		return result;
	}

	@Override
	public void patrol() {

	}
}
