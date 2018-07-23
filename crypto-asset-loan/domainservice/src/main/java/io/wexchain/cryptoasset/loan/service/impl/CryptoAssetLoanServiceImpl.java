package io.wexchain.cryptoasset.loan.service.impl;

import com.godmonth.status.executor.intf.OrderExecutor;
import com.weihui.basic.util.marshaller.json.JsonUtil;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeException;
import com.wexmarket.topia.commons.basic.exception.ErrorCodeValidate;
import com.wexmarket.topia.commons.data.page.PageUtils;
import com.wexmarket.topia.commons.pagination.Direction;
import com.wexmarket.topia.commons.pagination.PageParam;
import com.wexmarket.topia.commons.pagination.Pagination;
import com.wexmarket.topia.commons.pagination.SortParam;
import com.wexyun.open.api.domain.regular.loan.RegularPrepaymentBill;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;
import com.wexyun.open.api.enums.BillStatus;
import com.wexyun.open.api.util.ByteUtil;
import com.wexyun.open.api.util.SHA256Util;
import io.wexchain.cryptoasset.loan.api.ApplyRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanOrderPageRequest;
import io.wexchain.cryptoasset.loan.api.QueryLoanReportRequest;
import io.wexchain.cryptoasset.loan.api.constant.CalErrorCode;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.constant.LoanType;
import io.wexchain.cryptoasset.loan.api.constant.MortgageLoanOrderStatus;
import io.wexchain.cryptoasset.loan.api.model.Bill;
import io.wexchain.cryptoasset.loan.api.model.LoanReport;
import io.wexchain.cryptoasset.loan.api.model.OrderIndex;
import io.wexchain.cryptoasset.loan.api.model.RepaymentBill;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.ext.integration.configuration.LoanSdkConfiguration;
import io.wexchain.cryptoasset.loan.repository.LoanOrderRepository;
import io.wexchain.cryptoasset.loan.repository.query.LoanOrderQueryBuilder;
import io.wexchain.cryptoasset.loan.service.CollectOrderService;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.model.MortgageOrder;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import io.wexchain.dcc.cert.sdk.contract.CertData;
import io.wexchain.dcc.loan.sdk.contract.Agreement;
import io.wexchain.dcc.loan.sdk.contract.MortgageLoanOrder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Service
public class CryptoAssetLoanServiceImpl implements CryptoAssetLoanService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private LoanOrderRepository loanOrderRepository;

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

	private static final char[] HEX_CHAR =
			{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	@Override
	public LoanOrder advanceByChainOrderId(Long chainOrderId) {
		return loanOrderExecutor.execute(getLoanOrderByChainOrderId(chainOrderId), null, null).getModel();
	}

	@Override
	public LoanOrder advance(Long orderId) {
		return loanOrderExecutor.execute(getLoanOrder(orderId), null, null).getModel();
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
		LoanOrder loanOrder = loanOrderRepository.findByChainOrderIdAndMemberId(
				index.getChainOrderId(), index.getMemberId());
		return Optional.ofNullable(loanOrder);
	}

	@Override
	public LoanOrder getLoanOrderByChainOrderId(Long chainOrderId) {
		return ErrorCodeValidate.notNull(loanOrderRepository.findByChainOrderId(chainOrderId),
				CalErrorCode.ORDER_NOT_FOUND);
	}

	@Override
	public LoanOrder getLoanOrderByOrderIndex(OrderIndex index) {
		return ErrorCodeValidate.notNull(loanOrderRepository.findByChainOrderIdAndMemberId(
				index.getChainOrderId(), index.getMemberId()),
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
		io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder =
			chainOrderService.getLoanOrder(orderIndex.getChainOrderId());

		// 验证摘要
		checkIdDigest(applyRequest, chainLoanOrder);
		//checkApplicationDigest(applyRequest, chainLoanOrder);

		Map<String, String> imageExtParam = uploadImageFile(applyRequest);

		LoanOrder loanOrder = getLoanOrderByOrderIndexNullable(orderIndex).orElseGet(() -> {
			// 创建新订单
			LoanOrder order = new LoanOrder();
			order.setChainOrderId(orderIndex.getChainOrderId());
			order.setAssetCode(applyRequest.getAssetCode());
			order.setAmount(applyRequest.getAmount());
			order.setMemberId(orderIndex.getMemberId());
			order.setStatus(LoanOrderStatus.CREATED);
			order.setReceiverAddress(chainLoanOrder.getReceiveAddress());
			order.setChainSync(false);

			Map<String, String> extParam = order.getExtParam();
			extParam.put(LoanOrderExtParamKey.BORROW_DURATION, applyRequest.getBorrowDuration().toString());
			extParam.put(LoanOrderExtParamKey.BORROW_DURATION_UNIT, applyRequest.getDurationUnit().name());
			extParam.put(LoanOrderExtParamKey.APPLY_DATE, applyRequest.getApplyDate().toString());
			extParam.put(LoanOrderExtParamKey.LOAN_TYPE, applyRequest.getLoanType());
			extParam.put(LoanOrderExtParamKey.LOAN_PRODUCT_ID, applyRequest.getLoanProductId());
			extParam.put(LoanOrderExtParamKey.APP_IDENTITY, applyRequest.getAppIdentity());
			extParam.put(LoanOrderExtParamKey.EXPECT_ANNUAL_RATE, applyRequest.getExpectAnnualRate());
			extParam.put(LoanOrderExtParamKey.REPAY_MODE, applyRequest.getRepayMode());
			extParam.put(LoanOrderExtParamKey.LOAN_FEE,
					String.valueOf(AmountScaleUtil.cah2Cal(chainLoanOrder.getFee())));

			extParam.put(LoanOrderExtParamKey.BORROW_DURATION, applyRequest.getBorrowDuration().toString());

			extParam.put(LoanOrderExtParamKey.BORROW_NAME, applyRequest.getBorrowName());
			extParam.put(LoanOrderExtParamKey.BANK_CARD_NO, applyRequest.getBankCardNo());
			extParam.put(LoanOrderExtParamKey.ID_NO, applyRequest.getCertNo());
			extParam.put(LoanOrderExtParamKey.MOBILE, applyRequest.getMobile());
			extParam.put(LoanOrderExtParamKey.BANK_MOBILE, applyRequest.getBankMobile());

			extParam.putAll(imageExtParam);

			return loanOrderRepository.save(order);
		});

		return loanOrderExecutor.execute(loanOrder, null, null).getModel();
	}

	private Map<String, String> uploadImageFile(ApplyRequest applyRequest) {
		if (StringUtils.isNotEmpty(applyRequest.getIdCardFrontPic())) {

			logger.info("Id front image base64:{}",
					StringUtils.abbreviate(applyRequest.getIdCardFrontPic(), 80));
			logger.info("Id back image base64:{}",
					StringUtils.abbreviate(applyRequest.getIdCardBackPic(), 80));
			logger.info("Face front image base64:{}",
					StringUtils.abbreviate(applyRequest.getFacePic(), 80));

			File front = decryptByBase64(applyRequest.getIdCardFrontPic());
			File back = decryptByBase64(applyRequest.getIdCardBackPic());
			File face = decryptByBase64(applyRequest.getFacePic());

			String frontPath = wexyunLoanClient.uploadImageFile(front);
			logger.info("name:{}, front path:{}", applyRequest.getBorrowName(), frontPath);
			String backPath = wexyunLoanClient.uploadImageFile(back);
			logger.info("name:{}, back path:{}", applyRequest.getBorrowName(), backPath);
			String facePath = wexyunLoanClient.uploadImageFile(face);
			logger.info("name:{}, face path:{}", applyRequest.getBorrowName(), facePath);

			deleteTempImg(front);
			deleteTempImg(back);
			deleteTempImg(face);

			Map<String, String> map = new HashMap<>(3);
			map.put(LoanOrderExtParamKey.CERT_FRONT, frontPath);
			map.put(LoanOrderExtParamKey.CERT_BACK, backPath);
			map.put(LoanOrderExtParamKey.FACE_FRONT, facePath);
			return map;
		}
		return Collections.emptyMap();
	}

	private void deleteTempImg(File file) {
		if (!file.delete()) {
			logger.warn("Delete tmp file fail, file path:{}", file.getName());
		}
	}

	private File decryptByBase64(String base64) {
		try {
			String tmp = System.getProperty("java.io.tmpdir");
			String fileName = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
			String filePath = tmp + "/" + fileName + ".jpeg";
			Path path = Files.write(Paths.get(filePath), Base64.getDecoder().decode(base64),
					StandardOpenOption.CREATE);
			return path.toFile();
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
	}


	private void checkIdDigest(ApplyRequest applyRequest,
							   io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder) {
		byte[] idBytes = (applyRequest.getBorrowName() + applyRequest.getCertNo()).getBytes(StandardCharsets.UTF_8);
		byte[] idHash = DigestUtils.sha256(idBytes);
		if (!Arrays.equals(idHash, chainLoanOrder.getIdHash())) {
            throw new ErrorCodeException(CalErrorCode.DIGEST_NOT_MATCH.name(), "身份摘要不匹配");
        }
	}

	private void checkApplicationDigest(ApplyRequest applyRequest,
										io.wexchain.dcc.loan.sdk.contract.LoanOrder chainLoanOrder) {
		String applicationStr = "BitExpress"
				+ applyRequest.getAssetCode()
				+ applyRequest.getAmount().setScale(4, BigDecimal.ROUND_DOWN).toString()
				+ applyRequest.getBorrowDuration() + "D"
				+ 1 // 借款期数
				+ applyRequest.getLoanType()
				+ applyRequest.getApplyDate()
				+ applyRequest.getIdCardFrontPic()
				+ applyRequest.getIdCardBackPic()
				+ applyRequest.getFacePic();

		byte[] applicationHash = DigestUtils.sha256(applicationStr.getBytes(StandardCharsets.UTF_8));

		logger.info("进件申请摘要字符串: {}", StringUtils.abbreviate(applicationStr, 100));
		logger.info("链上申请hash: {}", bytesToHex(chainLoanOrder.getApplicationDigest()));
		logger.info("计算申请hash: {}", bytesToHex(applicationHash));

		if (!Arrays.equals(applicationHash, chainLoanOrder.getApplicationDigest())) {
			throw new ErrorCodeException(CalErrorCode.AUDIT_NOTHING.name(), "进件失败。");
		}
	}


	public static String bytesToHex(byte[] bytes) {
		// 一个byte为8位，可用两个十六进制位标识
		char[] buf = new char[bytes.length * 2];
		int a = 0;
		int index = 0;
		for(byte b : bytes) { // 使用除与取余进行转换
			if(b < 0) {
				a = 256 + b;
			} else {
				a = b;
			}

			buf[index++] = HEX_CHAR[a / 16];
			buf[index++] = HEX_CHAR[a % 16];
		}

		return new String(buf);
	}

	@Override
	public Page<LoanOrder> queryLoanOrderPage(QueryLoanOrderPageRequest request) {
		request.getSortPageParam()
				.setSortParamList(Collections.singletonList(new SortParam(Direction.DESC, "createdTime")));
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
		BigDecimal repayAddressBalance = AmountScaleUtil
				.cah2Cal(cahFunction.getBalance(loanOrder.getRepayAddress(), loanOrder.getAssetCode()));
		if (repaymentBill.getAmount().compareTo(repayAddressBalance) > 0) {
			repaymentBill.setNoPayAmount(repaymentBill.getAmount().subtract(repayAddressBalance));
		} else {
			repaymentBill.setNoPayAmount(BigDecimal.ZERO);
		}

		return repaymentBill;

	}

	@Override
	public List<LoanReport> queryLoanReport(QueryLoanReportRequest queryLoanReportRequest) {
		CertData data = chainOrderService.getCertData(queryLoanReportRequest.getAddress());
		byte[] idHash = data.getContent().getDigest1();

		Pagination<Agreement> agreementPagination = chainOrderService.queryAgreementPageByIdHashIndex(
				idHash, new PageParam(0, Integer.MAX_VALUE));
		List<LoanReport> loanReports = new ArrayList<>();
		if(agreementPagination.getItems() != null){
			for (int i = agreementPagination.getItems().size() - 1; i >= 0; i--) {
				LoanReport loanReport = new LoanReport();
				Agreement agreement = agreementPagination.getItems().get(i);
				Boolean isMatching = false;
				if(agreement.getBorrower().equalsIgnoreCase(queryLoanReportRequest.getAddress())){
					isMatching = true;
				}
				//普通借贷
				if(agreement.getCaller().equals(loanSdkConfiguration.getLoanAddress())){
					setLoanReportOrderInfo(loanReport, new OrderIndex(agreement.getOrderId(),queryLoanReportRequest.getMemberId()),isMatching);
					loanReport.setLoanType(LoanType.LOAN);
				}
				//抵押借贷
				if(agreement.getCaller().equals(loanSdkConfiguration.getMortgageLoanAddress())){
					setMortgageLoanReportOrderInfo(loanReport,agreement.getOrderId(),isMatching);
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
		return loanOrderRepository.countYesterdayDeliver(from.getTime(),to.getTime());

	}

	private void setMortgageLoanReportOrderInfo(LoanReport loanReport, long orderId, Boolean isMatching) {
		loanReport.setChainOrderId(orderId);

		MortgageLoanOrder mortgageLoanOrder = chainOrderService.getMortgageLoanOrder(loanReport.getChainOrderId());
		if(mortgageLoanOrder != null){
			if(mortgageLoanOrder.getContent() != null){
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
					loanReport.setBorrowDuration(new Interval(startDate.getTime(),repaymentDate.getTime()));
					loanReport.setDeliverDept(mortgageOrder.getDeliverDept());
					List<Bill> bills = new ArrayList<>();
					if(isMatching){
						Bill bill = new Bill();
						bill.setAmount(mortgageOrder.getRepaymentInterest().add(mortgageOrder.getRepaymentPrincipal()));
						bill.setExpectRepayDate(mortgageOrder.getLastRepaymentTime());
						bill.setNumber("1");
						bills.add(bill);
					} else {
						bills = null;
					}
					loanReport.setBillList(bills);
				}catch (Exception e){
					return;
				}
			}
			loanReport.setMortgageStatus(MortgageLoanOrderStatus.valueOf(mortgageLoanOrder.getStatus().name()));
		}

	}

	private void setLoanReportOrderInfo(LoanReport loanReport, OrderIndex index, boolean isMatching){
		getLoanOrderByChainOrderIdNullable(index.getChainOrderId()).ifPresent(loanOrder -> {
			loanReport.setLoanProductId(loanOrder.getExtParam().get(LoanOrderExtParamKey.LOAN_PRODUCT_ID));
			loanReport.setChainOrderId(index.getChainOrderId());
			loanReport.setDeliverDate(new Date(Long.parseLong(loanOrder.getExtParam().get(LoanOrderExtParamKey.DELIVER_DATE))));
			loanReport.setApplyDate(new Date(Long.parseLong(loanOrder.getExtParam().get(LoanOrderExtParamKey.APPLY_DATE))));
			loanReport.setAmount(loanOrder.getAmount());
			loanReport.setAssetCode(loanOrder.getAssetCode());
			loanReport.setStatus(loanOrder.getStatus());

			List<RepaymentPlan> repaymentPlans = wexyunLoanClient.queryRepaymentPlan(loanOrder.getApplyId());

			Date startDate = repaymentPlans.get(0).getBillStartDate();
			Date repaymentDate = repaymentPlans.get(repaymentPlans.size()-1).getLastRepaymentTime();
			loanReport.setBorrowDuration(new Interval(startDate.getTime(),repaymentDate.getTime()));
			List<Bill> bills = new ArrayList<>();
			if (loanOrder.getStatus() != LoanOrderStatus.CREATED && loanOrder.getStatus() != LoanOrderStatus.REJECTED) {
				if(isMatching){
					for (RepaymentPlan repaymentPlan : repaymentPlans) {
						if (repaymentPlan.getStatus() != BillStatus.CANCELED) {
							Bill bill = new Bill();
							bill.setAmount(AmountScaleUtil.wexyun2Cal(repaymentPlan.getAmount()));
							bill.setActualRepayDate(repaymentPlan.getRepaymentTime());
							bill.setExpectRepayDate(repaymentPlan.getLastRepaymentTime());
							bill.setNumber("1");
							bill.setStatus(io.wexchain.cryptoasset.loan.api.constant.BillStatus.valueOf(repaymentPlan.getStatus().name()));
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

}
