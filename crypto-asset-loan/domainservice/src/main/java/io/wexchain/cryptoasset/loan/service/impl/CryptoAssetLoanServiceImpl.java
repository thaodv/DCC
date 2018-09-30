package io.wexchain.cryptoasset.loan.service.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.google.common.base.Function;
import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.data.page.PageUtils;
import com.wexmarket.topia.commons.data.rpc.PageTransformer;
import com.wexmarket.topia.commons.pagination.Direction;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.pagination.SortParam;
import com.wexyun.open.api.domain.member.Member;
import com.wexyun.open.api.domain.regular.loan.RegularPrepaymentBill;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;
import com.wexyun.open.api.enums.BillStatus;
import com.wexyun.open.api.request.member.inner.InnerPersonalMemberInfoAddRequest;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.loan.api.*;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.constant.LoanType;
import io.wexchain.cryptoasset.loan.api.constant.MortgageLoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.model.Bill;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.cryptoasset.loan.api.product.LoanProduct;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.RetryableCommand;
import io.wexchain.cryptoasset.loan.ext.integration.configuration.LoanSdkConfiguration;
import io.wexchain.cryptoasset.loan.repository.AuditingOrderRepository;
import io.wexchain.cryptoasset.loan.repository.LoanOrderRepository;
import io.wexchain.cryptoasset.loan.repository.RetryableCommandRepository;
import io.wexchain.cryptoasset.loan.repository.query.LoanOrderQueryBuilder;
import io.wexchain.cryptoasset.loan.service.CollectOrderService;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import io.wexchain.cryptoasset.loan.service.LoanProductService;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.model.MortgageOrder;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.util.AddressUtil;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.loan.sdk.contract.Agreement;
import io.wexchain.dcc.loan.sdk.contract.MortgageLoanOrder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CryptoAssetLoanServiceImpl implements CryptoAssetLoanService {

	private static final String NOT_EXIST_MEMBER_ID = "-1";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private LoanOrderRepository loanOrderRepository;

	@Autowired
	private AuditingOrderRepository auditingOrderRepository;

	@Resource(name = "loanOrderExecutor")
	private OrderExecutor<LoanOrder, LoanOrderInstruction> loanOrderExecutor;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Autowired
	private CollectOrderService collectOrderService;

	@Autowired
	private CahFunction cahFunction;

	@Autowired
	private ChainOrderService chainOrderService;

	@Autowired
	private LoanSdkConfiguration loanSdkConfiguration;

	@Autowired
	private LoanProductService loanProductService;

	@Autowired
	private RetryableCommandRepository retryableCommandRepository;

	@Override
	public LoanOrder advanceByChainOrderId(Long chainOrderId) {
		return loanOrderExecutor.execute(getLoanOrderByChainOrderId(chainOrderId), null, null).getModel();
	}

	@Override
	public LoanOrder advance(Long orderId) {
		LoanOrder loanOrder = getLoanOrder(orderId);
		loanOrderExecutor.executeAsync(loanOrder, null, null);
		return loanOrder;
	}

	@Override
	public void advanceByApplyIdAsync(String applyId) {
		loanOrderExecutor.executeAsync(getLoanOrderByApplyId(applyId), null, null);
	}

	@Override
	public LoanOrder getLoanOrder(Long orderId) {
		return ErrorCodeValidate.notNull(loanOrderRepository.findById(orderId).orElse(null),
				CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public Optional<LoanOrder> getLoanOrderByChainOrderIdNullable(Long chainOrderId) {
		LoanOrder loanOrder = loanOrderRepository.findByChainOrderId(chainOrderId);
		return Optional.ofNullable(loanOrder);
	}

	@Override
	public Optional<LoanOrder> getLoanOrderByOrderIndexNullable(OrderIndex index) {
		LoanOrder loanOrder = loanOrderRepository.findByChainOrderIdAndMemberId(index.getChainOrderId(),
				index.getMemberId());
		return Optional.ofNullable(loanOrder);
	}

	@Override
	public LoanOrder getLoanOrderByChainOrderId(Long chainOrderId) {
		return ErrorCodeValidate.notNull(loanOrderRepository.findByChainOrderId(chainOrderId),
				CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public LoanOrder getLoanOrderByOrderIndex(OrderIndex index) {
		return ErrorCodeValidate.notNull(
				loanOrderRepository.findByChainOrderIdAndMemberId(index.getChainOrderId(), index.getMemberId()),
				CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public void cancel(OrderIndex index) {
		// 取消链上订单
		chainOrderService.cancel(index.getChainOrderId());

		// 取消本地订单
		getLoanOrderByOrderIndexNullable(index).ifPresent(loanOrder -> {
			loanOrderExecutor.execute(loanOrder, LoanOrderInstruction.CANCEL, null);
		});
	}

	@Override
	public LoanOrder getLoanOrderByApplyId(String applyId) {
		return ErrorCodeValidate.notNull(loanOrderRepository.findByApplyId(applyId), CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public LoanOrder apply(ApplyRequest applyRequest) {

		OrderIndex orderIndex = applyRequest.getIndex();

		// 查询链上订单
		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder = chainOrderService
				.getLoanOrder(orderIndex.getChainOrderId());

		//根据会员id获取借款人地址 TODO 索引删除memberId后该逻辑删除
		if (orderIndex.getBorrowerAddress() == null){
			String borrowerAddress = wexyunLoanClient.getAddressById(orderIndex.getMemberId());
			orderIndex.setBorrowerAddress(borrowerAddress);
		}

		//判断借款人是否与链上匹配
		ErrorCodeValidate.isTrue(chainLoanOrder.getBorrower().equals(AddressUtil.getAddress(orderIndex.getBorrowerAddress())),CalErrorCode.BORROWER_ADDRESS_NOT_MATCH);

		// 创建借款订单
		LoanOrder loanOrder = getLoanOrderByOrderIndexNullable(orderIndex).orElseGet(() -> {

			//判断云金融会员是否存在，不存在则创建
			Member member = wexyunLoanClient.getMemberByIdentity(orderIndex.getBorrowerAddress());
			if(member == null){
				member = ErrorCodeValidate.notNull(wexyunLoanClient.register(orderIndex.getBorrowerAddress()),CalErrorCode.CREATE_MEMBER_FAIL);
			}

			LoanOrder order = new LoanOrder();
			order.setChainOrderId(orderIndex.getChainOrderId());
			order.setAssetCode(applyRequest.getAssetCode());
			order.setAmount(applyRequest.getAmount());
			order.setMemberId(member.getMemberId());
			order.setBorrowerAddress(orderIndex.getBorrowerAddress());
			order.setStatus(LoanOrderStatus.CREATED);
			order.setReceiverAddress(chainLoanOrder.getReceiveAddress());
			order.setChainSync(false);

			// 设置进件参数
			Map<String, String> extParam = order.getExtParam();
			// 身份信息
			extParam.put(LoanOrderExtParamKey.BORROWER_NAME, applyRequest.getBorrowerName());
			extParam.put(LoanOrderExtParamKey.ID_CARD_NO, applyRequest.getIdCardNo());
			extParam.put(LoanOrderExtParamKey.ID_CARD_FRONT_PIC_UFS_PATH, applyRequest.getIdCardFrontPicUfsPath());
			extParam.put(LoanOrderExtParamKey.ID_CARD_BACK_PIC_UFS_PATH, applyRequest.getIdCardBackPicUfsPath());
			extParam.put(LoanOrderExtParamKey.FACE_PIC_UFS_PATH, applyRequest.getFacePicUfsPath());

			// 银行卡信息
			extParam.put(LoanOrderExtParamKey.BANK_CARD_NO, applyRequest.getBankCardNo());
			extParam.put(LoanOrderExtParamKey.BANK_CARD_MOBILE, applyRequest.getBankCardMobile());

			// 通话记录
			extParam.put(LoanOrderExtParamKey.MOBILE, applyRequest.getMobile());
			extParam.put(LoanOrderExtParamKey.COMMUNICATION_LOG, applyRequest.getCommunicationLog());

			// 申请数据
			extParam.put(LoanOrderExtParamKey.BORROW_DURATION, applyRequest.getBorrowDuration().toString());
			extParam.put(LoanOrderExtParamKey.BORROW_DURATION_UNIT, "DAY");
			extParam.put(LoanOrderExtParamKey.APPLY_DATE, String.valueOf(applyRequest.getApplyDate().getTime()));

			LoanProduct product = loanProductService.getLoanProductByCurrencySymbol(applyRequest.getAssetCode());
			extParam.put(LoanOrderExtParamKey.LOAN_TYPE, product.getLoanType());

			extParam.put(LoanOrderExtParamKey.LOAN_FEE,
					String.valueOf(AmountScaleUtil.cah2Cal(chainLoanOrder.getFee())));

			return loanOrderRepository.save(order);
		});

		loanOrderExecutor.executeAsync(loanOrder, null, null);

		return loanOrder;
	}

	@Override
	public Page<LoanOrder> queryLoanOrderPage(QueryLoanOrderPageRequest request) {
		request.getSortPageParam()
				.setSortParamList(Collections.singletonList(new SortParam(Direction.DESC, "createdTime")));

		handleRequestBorrowerAddress(request);



		PageRequest pageRequest = PageUtils.convert(request.getSortPageParam());
		return loanOrderRepository.findAll(LoanOrderQueryBuilder.query(request), pageRequest);
	}



	@Override
	public LoanOrder confirmRepayment(OrderIndex index) {
		LoanOrder loanOrder = loanOrderRepository.findByChainOrderIdAndMemberId(index.getChainOrderId(),
				index.getMemberId());
		ErrorCodeValidate.notNull(loanOrder, CalErrorCode.ORDER_NOT_FOUND);
		loanOrder = loanOrderExecutor.execute(loanOrder, null, null).getModel();

		// 执行归集
		collectOrderService.collect(loanOrder);
		return loanOrder;
	}

	@Override
	public RepaymentBill queryRepaymentBill(OrderIndex index) {
		LoanOrder loanOrder = getLoanOrderByOrderIndex(index);

		DateTime repayDate = new DateTime(Long.valueOf(loanOrder.getExtParam().get(LoanOrderExtParamKey.REPAY_DATE)));

		RepaymentBill repaymentBill = new RepaymentBill();
		repaymentBill.setLoanOrderId(loanOrder.getId());
		repaymentBill.setChainOrderId(loanOrder.getChainOrderId());
		repaymentBill.setApplyId(loanOrder.getApplyId());
		repaymentBill.setAssetCode(loanOrder.getAssetCode());
		repaymentBill.setLoanProductId(loanOrder.getExtParam().get(LoanOrderExtParamKey.LOAN_PRODUCT_ID));
		repaymentBill.setMemberId(loanOrder.getMemberId());
		repaymentBill.setRepaymentAddress(loanOrder.getRepayAddress());

		if (repayDate.isAfterNow()) {
			RepaymentPlan repaymentPlan = wexyunLoanClient.queryFirstRepaymentPlan(loanOrder.getApplyId());
			if (repaymentPlan.getStatus() == BillStatus.CREATED
					|| repaymentPlan.getStatus() == BillStatus.WAITING_VERIFY) {
				RegularPrepaymentBill bill = wexyunLoanClient.queryRegularPrepaymentBill(loanOrder.getApplyId());
				repaymentBill.setPenaltyAmount(AmountScaleUtil.wexyun2Cal(bill.getPenaltyAmount()));
				repaymentBill.setRepaymentInterest(AmountScaleUtil.wexyun2Cal(bill.getRepaymentInterest()));
				repaymentBill.setRepaymentPrincipal(AmountScaleUtil.wexyun2Cal(bill.getRepaymentPrincipal()));
				repaymentBill.setOverdueFine(AmountScaleUtil.wexyun2Cal(bill.getOverdueFine()));
				repaymentBill.setAmount(AmountScaleUtil.wexyun2Cal(bill.getAmount()));
			} else {
				repaymentBill.setPenaltyAmount(BigDecimal.ZERO);
				repaymentBill.setRepaymentInterest(AmountScaleUtil.wexyun2Cal(repaymentPlan.getRepaymentInterest()));
				repaymentBill.setRepaymentPrincipal(AmountScaleUtil.wexyun2Cal(repaymentPlan.getRepaymentPrincipal()));
				repaymentBill.setOverdueFine(AmountScaleUtil.wexyun2Cal(repaymentPlan.getOverdueFine()));
				repaymentBill.setAmount(AmountScaleUtil.wexyun2Cal(repaymentPlan.getAmount()));
			}
		} else {
			RepaymentPlan repaymentPlan = wexyunLoanClient.queryFirstRepaymentPlan(loanOrder.getApplyId());
			repaymentBill.setPenaltyAmount(BigDecimal.ZERO);
			repaymentBill.setRepaymentInterest(AmountScaleUtil.wexyun2Cal(repaymentPlan.getRepaymentInterest()));
			repaymentBill.setRepaymentPrincipal(AmountScaleUtil.wexyun2Cal(repaymentPlan.getRepaymentPrincipal()));
			repaymentBill.setOverdueFine(AmountScaleUtil.wexyun2Cal(repaymentPlan.getOverdueFine()));
			repaymentBill.setAmount(AmountScaleUtil.wexyun2Cal(repaymentPlan.getAmount()));
		}


		BigInteger balance = cahFunction.getBalance(loanOrder.getRepayAddress(), loanOrder.getAssetCode());
		BigInteger billAmount = AmountScaleUtil.cal2Cah(repaymentBill.getAmount());

		if (billAmount.compareTo(balance) > 0) {
			BigInteger subtract = billAmount.subtract(balance);
			BigDecimal divide = new BigDecimal(subtract).divide(BigDecimal.ONE.scaleByPowerOfTen(18), 18, RoundingMode.DOWN);
			if (divide.compareTo(new BigDecimal("0.0001")) < 0) {
				repaymentBill.setNoPayAmount(new BigDecimal("0.0001"));
			} else {
				repaymentBill.setNoPayAmount(divide.setScale(4, RoundingMode.UP));
			}
		} else {
			repaymentBill.setNoPayAmount(BigDecimal.ZERO);
		}

		return repaymentBill;

	}

	@Override
	public List<LoanReport> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest) {
		CertData data = chainOrderService.getCertData(queryLoanReportRequest.getAddress());
		byte[] idHash = data.getContent().getDigest1();

		Pagination<Agreement> agreementPagination = chainOrderService.queryAgreementPageByIdHashIndex(idHash,
				new PageParam(0, Integer.MAX_VALUE));
		List<LoanReport> loanReports = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(agreementPagination.getItems())) {
			for (int i = agreementPagination.getItems().size() - 1; i >= 0; i--) {
				LoanReport loanReport = new LoanReport();
				Agreement agreement = agreementPagination.getItems().get(i);
				Boolean isMatching = false;
				if (agreement.getBorrower().equalsIgnoreCase(queryLoanReportRequest.getAddress())) {
					isMatching = true;
				}
				// 普通借贷
				if (agreement.getCaller().equals(loanSdkConfiguration.getLoanAddress())) {
					setLoanReportOrderInfo(loanReport, agreement.getOrderId(), isMatching);
					loanReport.setLoanType(LoanType.LOAN);
				}
				// 抵押借贷
				if (agreement.getCaller().equals(loanSdkConfiguration.getMortgageLoanAddress())) {
					setMortgageLoanReportOrderInfo(loanReport, agreement.getOrderId(), isMatching);
					loanReport.setLoanType(LoanType.MORTGAGE);
				}
				loanReport.setBorrowerAddress(agreement.getBorrower());
				loanReports.add(loanReport);
			}
		}
		return loanReports;
	}

	@Override
	public Integer queryYesterdayDeliverCount() {
		Date from = DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate();
		Date to = DateTime.now().withTimeAtStartOfDay().minusMillis(1).toDate();
		return loanOrderRepository.countYesterdayDeliver(from.getTime(), to.getTime());

	}

    @Override
    public BigDecimal getTotalDeliverAmount(DeliverAmountRequest request) {
        BigDecimal totalDeliverAmount = loanOrderRepository.getTotalDeliverAmount(request.getStartTime().toDate(),
                request.getEndTime().toDate(),
                request.getAssetCode());
        if(totalDeliverAmount == null){
            return BigDecimal.ZERO;
        }
        return totalDeliverAmount;
    }

	@Override
	public List<Long> addBorrowerAddress() {
		List<LoanOrder> loanOrders = loanOrderRepository.findByBorrowerAddressIsNull();
		if(loanOrders != null){
			List<Long> failLoanOrder = new ArrayList<>();
			//补充数据
			loanOrders.forEach(loanOrder -> {
				try {
					String memberId = loanOrder.getMemberId();
					//查询地址
					String borrowerAddress = wexyunLoanClient.getAddressById(memberId);
					loanOrder.setBorrowerAddress(borrowerAddress);
					loanOrderRepository.save(loanOrder);
				}catch (Exception e){
					failLoanOrder.add(loanOrder.getId());
				}
			});
			return failLoanOrder;
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public Pagination<LoanReport> queryLoanReportByIdentity(IdentityRequest identityRequest) {
		byte[] idHash = getIdHash(identityRequest);

		Pagination<Agreement> agreementPagination = chainOrderService.queryAgreementPageByIdHashIndex(idHash,
				identityRequest.getPageParam());
		if (logger.isDebugEnabled()) {
			logger.debug("idHash:{},agreement.page:{},{}", "0x" + Hex.encodeHexString(idHash),
					agreementPagination.getSortPageParam().getNumber(),
					agreementPagination.getSortPageParam().getSize());
		}
		return PageTransformer.transform(agreementPagination, new Function<Agreement, LoanReport>() {

			@Override
			public LoanReport apply(Agreement agreement) {
				LoanReport loanReport = new LoanReport();
				// 普通借贷
				if (agreement.getCaller().equals(loanSdkConfiguration.getLoanAddress())) {
					setLoanReportOrderInfo(loanReport, agreement.getOrderId(), true);
					loanReport.setLoanType(LoanType.LOAN);
				}
				// 抵押借贷
				if (agreement.getCaller().equals(loanSdkConfiguration.getMortgageLoanAddress())) {
					setMortgageLoanReportOrderInfo(loanReport, agreement.getOrderId(), true);
					loanReport.setLoanType(LoanType.MORTGAGE);
				}
				loanReport.setBorrowerAddress(agreement.getBorrower());
				return loanReport;
			}
		});

	}

    @Override
    public AuditingOrder getAuditingOrder(Long orderId) {
        return auditingOrderRepository.findById(orderId).get();
    }

	@Override
	public AuditingOrder getAuditingOrderNullable(Long orderId) {
		return auditingOrderRepository.findById(orderId).orElse(null);
	}

    @Override
    public void handleTransferOrder(TransferOrder transferOrder) {
		Optional<RetryableCommand> commandOpt =
				retryableCommandRepository.findById(Long.valueOf(transferOrder.getRequestIdentity().getRequestNo()));

		if (!commandOpt.isPresent()) {
			logger.info("Not found command of transfer order message, id:{}, request no:{}",
					transferOrder.getId(), transferOrder.getRequestIdentity().getRequestNo());
		} else {
			RetryableCommand command = commandOpt.get();
			switch (command.getParentType()) {
				case LoanOrder.TYPE_REF: {
					logger.info("Advance loan order:{}", command.getParentId());
					advance(command.getParentId());
				}
			}
		}
	}

    private void setMortgageLoanReportOrderInfo(LoanReport loanReport, long orderId, Boolean isMatching) {
		loanReport.setChainOrderId(orderId);

		MortgageLoanOrder mortgageLoanOrder = chainOrderService.getMortgageLoanOrder(loanReport.getChainOrderId());
		if (mortgageLoanOrder != null) {
			if (mortgageLoanOrder.getContent() != null) {
				try {
					MortgageOrder mortgageOrder = JsonUtil.parse(mortgageLoanOrder.getContent(), MortgageOrder.class);
					loanReport.setDeliverDate(mortgageOrder.getDeliverTime());
					loanReport.setApplyDate(mortgageOrder.getApplyTime());
					loanReport.setAmount(mortgageOrder.getBorrowerAmount());
					loanReport.setAssetCode(mortgageOrder.getBorrowerUnit().toUpperCase());
					loanReport.setMortgageAmount(mortgageOrder.getMortgageAmount());
					loanReport.setMortgageUnit(mortgageOrder.getMortgageUnit().toUpperCase());
					Date startDate = mortgageOrder.getBillStartDate();
					Date repaymentDate = mortgageOrder.getLastRepaymentTime();
					loanReport.setBorrowDuration(new Interval(startDate.getTime(), repaymentDate.getTime()));
					loanReport.setDeliverDept(mortgageOrder.getDeliverDept());
					List<Bill> bills = new ArrayList<>();
					if (isMatching) {
						Bill bill = new Bill();
						bill.setAmount(mortgageOrder.getRepaymentInterest().add(mortgageOrder.getRepaymentPrincipal()));
						bill.setExpectRepayDate(mortgageOrder.getLastRepaymentTime());
						bill.setNumber("1");
						bills.add(bill);
					} else {
						bills = null;
					}
					loanReport.setBillList(bills);
				} catch (Exception e) {
					logger.error("构建抵押借贷报告错误：chainOrderId:[{}] , 错误信息[{}]",orderId,e);
					return;
				}
			}
			loanReport.setMortgageStatus(MortgageLoanOrderStatus.valueOf(mortgageLoanOrder.getStatus().name()));
		}

	}

	private void setLoanReportOrderInfo(LoanReport loanReport, Long chainOrderId, boolean isMatching) {
		getLoanOrderByChainOrderIdNullable(chainOrderId).ifPresent(loanOrder -> {
			//loanReport.setLoanProductId(loanOrder.getExtParam().get(LoanOrderExtParamKey.LOAN_PRODUCT_ID));
			loanReport.setChainOrderId(chainOrderId);
			if (loanOrder.getExtParam().get(LoanOrderExtParamKey.DELIVER_DATE) != null) {
				loanReport.setDeliverDate(
						new Date(Long.parseLong(loanOrder.getExtParam().get(LoanOrderExtParamKey.DELIVER_DATE))));
			}
			if (loanOrder.getExtParam().get(LoanOrderExtParamKey.APPLY_DATE) != null) {
				loanReport.setApplyDate(
						new Date(Long.parseLong(loanOrder.getExtParam().get(LoanOrderExtParamKey.APPLY_DATE))));
			}

			loanReport.setAmount(loanOrder.getAmount());
			loanReport.setAssetCode(loanOrder.getAssetCode());
			loanReport.setStatus(loanOrder.getStatus());

			List<RepaymentPlan> repaymentPlans = wexyunLoanClient.queryRepaymentPlan(loanOrder.getApplyId());
			Date startDate = repaymentPlans.get(0).getBillStartDate();
			Date repaymentDate = repaymentPlans.get(repaymentPlans.size() - 1).getLastRepaymentTime();
			loanReport.setBorrowDuration(new Interval(startDate.getTime(), repaymentDate.getTime()));
			List<Bill> bills = new ArrayList<>();
			if (loanOrder.getStatus() != LoanOrderStatus.CREATED && loanOrder.getStatus() != LoanOrderStatus.REJECTED) {
				if (isMatching) {
					for (RepaymentPlan repaymentPlan : repaymentPlans) {
						if (repaymentPlan.getStatus() != BillStatus.CANCELED) {
							Bill bill = new Bill();
							bill.setAmount(AmountScaleUtil.wexyun2Cal(repaymentPlan.getAmount()));
							bill.setActualRepayDate(repaymentPlan.getRepaymentTime());
							bill.setExpectRepayDate(repaymentDate);
							bill.setNumber("1");
							bill.setStatus(io.wexchain.cryptoasset.loan.api.constant.BillStatus
									.valueOf(repaymentPlan.getStatus().name()));
							bills.add(bill);
						}
					}
				} else {
					bills = null;
				}
			}
			loanReport.setBillList(bills);
		});
	}

	private byte[] getIdHash(IdentityRequest identityRequest) {

		byte[] idBytes = (identityRequest.getRealName() + identityRequest.getCertNo()).getBytes(StandardCharsets.UTF_8);
		byte[] idHash = DigestUtils.sha256(idBytes);
		return idHash;
	}

	private void handleRequestBorrowerAddress(QueryLoanOrderPageRequest request) {
		if (StringUtils.isNotEmpty(request.getBorrowerAddress()) && StringUtils.isEmpty(request.getMemberId())) {
			Member member = wexyunLoanClient.getMemberByIdentity(request.getBorrowerAddress());
			if (member != null) {
				request.setMemberId(member.getMemberId());
			} else {
				request.setMemberId(NOT_EXIST_MEMBER_ID);
			}
		}
	}

}
