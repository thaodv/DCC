package io.wexchain.cryptoasset.loan.service.processor.order.loan.advancer;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.weihui.finance.contract.api.response.GeneratPDFFileResponse;
import com.wexyun.open.api.domain.file.DownloadFileInfo;
import com.wexyun.open.api.domain.regular.loan.RepaymentPlan;

import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.loan.api.constant.LoanOrderStatus;
import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
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

public class DeliverAdvancer extends AbstractAdvancer<LoanOrder, LoanOrderInstruction, LoanOrderTrigger> {

	{
		availableStatus = LoanOrderStatus.APPROVED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

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
		logger.debug("loanOrder:{}", loanOrder.getId());
		// 1 查询还款账单
		RepaymentPlan repaymentPlan = wexyunLoanClient.queryFirstRepaymentPlan(loanOrder.getApplyId());
		String billDigest = calcBillSha256(repaymentPlan);
		logger.debug("repaymentPlan:{}", repaymentPlan.getBillId());

		// 2 生成借款合同
		Credit2Apply applyOrder = wexyunLoanClient.getApplyOrder2(loanOrder.getApplyId());
		GeneratPDFFileResponse fileResp = wexyunLoanClient.generateAgreement(loanOrder, applyOrder, repaymentPlan);
		String agreementDigest = calcAgreementSha256(fileResp.getFilePath());

		// 3 放款
		RetryableCommand deliverCommand = executeDeliver(loanOrder);

		if (RetryableCommandHelper.isSuccess(deliverCommand)) {

			// 链上订单还款
			logger.info("billDigest:{}, agreementDigest:{}", billDigest, agreementDigest);

			chainOrderService.deliver(loanOrder.getChainOrderId(), billDigest, agreementDigest);
			logger.info("after deliver:{}", loanOrder.getId());

			String repayAddress = prepareRepayAddress(loanOrder);
			logger.info("after prepareRepayAddress:{}", loanOrder.getId());

			try {
				rebateService.deliverSuccess(loanOrder.getId());
				logger.info("after deliverSuccess:{}", loanOrder.getId());
			} catch (RuntimeException e) {
				logger.warn("skip rebateService.deliver:{},error:{}", loanOrder.getId(), e.getMessage());
				throw e;
			}

			return new AdvancedResult<>(new TriggerBehavior<>(LoanOrderTrigger.DELIVER, o -> {
				o.setRepayAddress(repayAddress);
				long start = repaymentPlan.getBillStartDate().getTime();
				long end = repaymentPlan.getLastRepaymentTime().getTime();
				o.getExtParam().put(LoanOrderExtParamKey.DELIVER_DATE,
						String.valueOf(deliverCommand.getCreatedTime().getTime()));
				o.getExtParam().put(LoanOrderExtParamKey.REPAY_DATE, String.valueOf(end));
				o.getExtParam().put(LoanOrderExtParamKey.AGREEMENT_PATH, fileResp.getFilePath());
				o.getExtParam().put(LoanOrderExtParamKey.AGREEMENT_ID, fileResp.getRecordId());
			}), NextOperation.PAUSE);
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
}
