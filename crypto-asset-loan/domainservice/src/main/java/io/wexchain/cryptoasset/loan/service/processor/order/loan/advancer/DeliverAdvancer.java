package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import com.alibaba.fastjson.JSON;
import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.weihui.finance.contract.api.response.GeneratPDFFileResponse;
import com.wexyun.open.api.domain.file.DownloadFileInfo;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;
import com.wexyun.open.api.response.BaseResponse;
import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.RebateOrder;
import io.wexchain.cryptoasset.loan.domain.RetryableCommand;
import io.wexchain.cryptoasset.loan.repository.LoanOrderRepository;
import io.wexchain.cryptoasset.loan.service.RebateService;
import io.wexchain.cryptoasset.loan.service.constant.CommandName;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.function.chain.ChainOrderService;
import io.wexchain.cryptoasset.loan.service.function.command.CommandIndex;
import io.wexchain.cryptoasset.loan.service.function.command.RetryableCommandHelper;
import io.wexchain.cryptoasset.loan.service.function.command.RetryableCommandTemplate;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderInstruction;
import io.wexchain.cryptoasset.loan.service.processor.order.loan.LoanOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import io.wexchain.dcc.loan.sdk.contract.OrderStatus;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class DeliverAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

	{
		availableStatus = LoanOrderStatus.APPROVED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final static int QUERY_REPAYMENT_PLAN_TIMES = 10;

	@Autowired
	private RetryableCommandTemplate retryableCommandTemplate;

	@Autowired
	private LoanOrderRepository loanOrderRepository;

	@Autowired
	private CahFunction cahFunction;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Autowired
	private ChainOrderService chainOrderService;

	@Autowired
	private RebateService rebateService;

	@Override
	public AdvancedResult<LoanOrder, LoanOrderTrigger> advance(LoanOrder loanOrder, LoanOrderInstruction instruction,
			Object message) {

		logger.info("Loan order delivery:{} --> start", loanOrder.getId());

		// 1 放款
		RetryableCommand deliverCommand = executeDeliver(loanOrder);

		if (RetryableCommandHelper.isSuccess(deliverCommand)) {

			// 1 云金融审核协议
			BaseResponse verifyResult = wexyunLoanClient.verifyAgreement(loanOrder.getApplyId(),
					loanOrder.getExtParam().get(LoanOrderExtParamKey.LOAN_TYPE));
			logger.info("Loan order delivery:{} --> verify agreement result:{}",
					loanOrder.getId(), JSON.toJSONString(verifyResult));

			// 2 查询还款账单
			RepaymentPlan repaymentPlan = queryRepaymentPlan(loanOrder);
			String billDigest = calcBillSha256(repaymentPlan);
			logger.info("Loan order delivery:{} --> query repayment plant, bill id:{}, bill status:{}",
					loanOrder.getId(), repaymentPlan.getBillId(), repaymentPlan.getStatus());

			// 3 生成借款合同
			Credit2Apply applyOrder = wexyunLoanClient.getApplyOrder2(loanOrder.getApplyId());
			GeneratPDFFileResponse fileResp = wexyunLoanClient.generateAgreement(loanOrder, applyOrder, repaymentPlan);
			String agreementDigest = calcAgreementSha256(fileResp.getFilePath());
			logger.info("Loan order delivery:{} --> cal agreement hash:{}",
					loanOrder.getId(), agreementDigest);

			// 4 链上订单放款
			chainOrderService.deliver(loanOrder.getChainOrderId(), billDigest, agreementDigest);
			logger.info("Loan order delivery:{} --> chain order repaid, chain order id:{}",
					loanOrder.getId(), loanOrder.getChainOrderId());

			if (chainOrderService.getLoanOrder(loanOrder.getChainOrderId()).getStatus() == OrderStatus.DELIVERIED) {
				// 5 分账
				try {
					rebateService.deliverSuccess(loanOrder.getId());
					logger.info("Loan order delivery:{} --> rebate", loanOrder.getId());
				} catch (RuntimeException e) {
					logger.warn("skip rebateService.deliver:{},error:{}", loanOrder.getId(), e.getMessage());
					throw e;
				}

				// 6 生成还款地址
				String repayAddress = prepareRepayAddress(loanOrder);
				logger.info("Loan order delivery:{} --> prepare repay address:{}",
						loanOrder.getId(), repayAddress);

				return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.DELIVER, o -> {
					o.setRepayAddress(repayAddress);
					long start = repaymentPlan.getBillStartDate().getTime();
					long end = repaymentPlan.getLastRepaymentTime().getTime();
					BigDecimal interest = AmountScaleUtil.wexyun2Cal(applyOrder.getDebtAgreement().getExpectInterest());
					o.getExtParam().put(LoanOrderExtParamKey.DELIVER_DATE,
							String.valueOf(deliverCommand.getCreatedTime().getTime()));
					o.getExtParam().put(LoanOrderExtParamKey.REPAY_DATE, String.valueOf(end));
					o.getExtParam().put(LoanOrderExtParamKey.AGREEMENT_PATH, fileResp.getFilePath());
					o.getExtParam().put(LoanOrderExtParamKey.AGREEMENT_ID, fileResp.getRecordId());
					o.getExtParam().put(LoanOrderExtParamKey.EXPECT_LOAN_INTEREST, interest.toString());
				}), NextOperation.PAUSE);
			}
		}
		return null;
	}

	private String calcBillSha256(RepaymentPlan repaymentPlan) {
		StringBuilder text = new StringBuilder();
		text.append(repaymentPlan.getLastRepaymentTime().getTime());
		String repaymentTime = "";
		if (repaymentPlan.getRepaymentTime() != null) {
			repaymentTime = repaymentPlan.getRepaymentTime().getTime() + "";
		}
		text.append(repaymentTime);
		text.append(repaymentPlan.getIssueNumber());
		return DigestUtils.sha256Hex(text.toString());
	}

	private String calcAgreementSha256(String filePath) {
		try {
			DownloadFileInfo fileInfo = wexyunLoanClient.downloadFile(filePath);
			return DigestUtils.sha256Hex(fileInfo.getInputStream());
		} catch (IOException e) {
			throw new ContextedRuntimeException(e);
		}
	}

	/**
	 * 放款
	 */
	private RetryableCommand executeDeliver(LoanOrder loanOrder) {
		CommandIndex commandIndex = new CommandIndex(LoanOrder.TYPE_REF, loanOrder.getId(), CommandName.CMD_DELIVER);
		return retryableCommandTemplate.execute(commandIndex,
				ci -> Validate.notNull(loanOrderRepository.lockById(ci.getParentId())), null, command -> {
					if (RetryableCommandHelper.isCreated(command)) {
						TransferOrder transferOrder = cahFunction.deliver(String.valueOf(command.getId()),
								loanOrder.getAmount(), loanOrder.getReceiverAddress(), loanOrder.getAssetCode());
						if (transferOrder.getStatus() == TransferOrderStatus.SUCCESS) {
							return GeneralCommandStatus.SUCCESS.name();
						} else if (transferOrder.getStatus() == TransferOrderStatus.FAILURE) {
							return GeneralCommandStatus.FAILURE.name();
						}
					}
					return command.getStatus();
				});
	}

	/**
	 * 生成还款地址
	 */
	private String prepareRepayAddress(LoanOrder loanOrder) {
		String repayAddress = loanOrder.getRepayAddress();
		if (StringUtils.isEmpty(repayAddress)) {
			repayAddress = cahFunction.createEthWallet().getAddress();
		}
		return repayAddress;
	}

	/**
	 * 轮询还款账单
	 */
	private RepaymentPlan queryRepaymentPlan(LoanOrder loanOrder) {
		for (int i = 0; i < QUERY_REPAYMENT_PLAN_TIMES; i++) {
			List<RepaymentPlan> repaymentPlanList = wexyunLoanClient.queryRepaymentPlan(loanOrder.getApplyId());
			logger.info("Apply id:{} ---> Query repayment plan, size:{}",
					loanOrder.getApplyId(), repaymentPlanList.size());
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (repaymentPlanList.size() > 0) {
				return repaymentPlanList.get(0);
			}
		}
		throw new ContextedRuntimeException("Loan order id:" + loanOrder.getId() + " query repayment plan fail");
	}
}
