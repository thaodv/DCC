package io.wexchain.cryptoasset.loan.service.processor.order.auditing.advancer.apply;

import com.godmonth.status.advancer.impl.AbstractAdvancer;
import com.godmonth.status.advancer.intf.AdvancedResult;
import com.godmonth.status.advancer.intf.NextOperation;
import com.godmonth.status.transitor.tx.intf.TriggerBehavior;
import com.wexyun.open.api.domain.credit2.Credit2ApplyAddResult;
import com.wexyun.open.api.domain.credit2.LoanMaterial;
import com.wexyun.open.api.enums.DurationType;
import com.wexyun.open.api.response.BaseResponse;
import com.wexyun.open.api.response.QueryResponse4Single;
import io.wexchain.cryptoasset.loan.api.constant.AuditingOrderStatus;
import io.wexchain.cryptoasset.loan.api.product.LoanProduct;
import io.wexchain.cryptoasset.loan.common.constant.GeneralCommandStatus;
import io.wexchain.cryptoasset.loan.domain.AuditingOrder;
import io.wexchain.cryptoasset.loan.domain.LoanOrder;
import io.wexchain.cryptoasset.loan.domain.UnretryableCommand;
import io.wexchain.cryptoasset.loan.service.CryptoAssetLoanService;
import io.wexchain.cryptoasset.loan.service.LoanProductService;
import io.wexchain.cryptoasset.loan.service.constant.CommandName;
import io.wexchain.cryptoasset.loan.service.constant.LoanOrderExtParamKey;
import io.wexchain.cryptoasset.loan.service.function.command.CommandIndex;
import io.wexchain.cryptoasset.loan.service.function.command.UnretryableCommandFunction;
import io.wexchain.cryptoasset.loan.service.function.wexyun.WexyunLoanClient;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2Apply;
import io.wexchain.cryptoasset.loan.service.function.wexyun.model.Credit2ApplyAddRequest;
import io.wexchain.cryptoasset.loan.service.processor.order.auditing.AuditingOrderTrigger;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Function;

public class ApplyAdvancer extends AbstractAdvancer<AuditingOrder, Void, AuditingOrderTrigger> {

	{
		availableStatus = AuditingOrderStatus.VALIDATED;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UnretryableCommandFunction unretryableCommandFunction;

	@Autowired
	private CryptoAssetLoanService cryptoAssetLoanService;

	@Autowired
	private WexyunLoanClient wexyunLoanClient;

	@Autowired
	private LoanProductService loanProductService;

	private List<Function<LoanOrder, UploadResult>> uploadFunctions;

	@Override
	public AdvancedResult<AuditingOrder, AuditingOrderTrigger> advance(AuditingOrder auditingOrder, Void instruction,
			Object message) {

		LoanOrder loanOrder = cryptoAssetLoanService.getLoanOrder(auditingOrder.getId());

		UnretryableCommand command = unretryableCommandFunction.prepareCommand(
				new CommandIndex(AuditingOrder.TYPE_REF, auditingOrder.getId(), CommandName.CMD_APPLY), null);

		if (command.getStatus().equals(GeneralCommandStatus.CREATED.name())) {
			// 查询进件申请
			QueryResponse4Single<Credit2Apply> queryResult = wexyunLoanClient.getApplyOrder(String.valueOf(command.getId()));
			if (queryResult.isSuccess()) {
				Credit2Apply applyOrder = queryResult.getContent();
				if (applyOrder == null) {
					return apply(command, loanOrder);
				} else {
					successfulApplication(command, applyOrder.getApplyId());
				}
			} else {
				return rejectApplication(command, queryResult);
			}
		}
		if (command.getStatus().equals(GeneralCommandStatus.SUCCESS.name())) {
			return new AdvancedResult<>(new TriggerBehavior<>(AuditingOrderTrigger.APPLY), NextOperation.PAUSE);
		}
		if (command.getStatus().equals(GeneralCommandStatus.FAILURE.name())) {
			return new AdvancedResult<>(new TriggerBehavior<>(AuditingOrderTrigger.REJECT));
		}
		return null;
	}

	public void setUploadFunctions(List<Function<LoanOrder, UploadResult>> uploadFunctions) {
		this.uploadFunctions = uploadFunctions;
	}

	private AdvancedResult<AuditingOrder, AuditingOrderTrigger> apply(UnretryableCommand command, LoanOrder loanOrder) {
		QueryResponse4Single<Credit2ApplyAddResult> applyResult =
				wexyunLoanClient.apply(buildRequest(loanOrder, command.getId()));
		if (applyResult.isSuccess()) {
			return successfulApplication(command, String.valueOf(applyResult.getContent().getApplyId()));
		} else {
			return rejectApplication(command, applyResult);
		}
	}

	private List<LoanMaterial> buildLoanMaterialList(LoanOrder loanOrder) {
		List<LoanMaterial> list = new ArrayList<>(3);
		for (Function<LoanOrder, UploadResult> function : uploadFunctions) {
			UploadResult uploadResult = function.apply(loanOrder);
			LoanMaterial loanMaterial = new LoanMaterial();
			loanMaterial.setFileUrl(uploadResult.getToken());
			loanMaterial.setItemCode(uploadResult.getWexyunMaterialKey());
			list.add(loanMaterial);
		}
		return list;
	}

	private Credit2ApplyAddRequest buildRequest(LoanOrder loanOrder, Long commandId) {

		LoanProduct loanProduct = loanProductService.getLoanProductByCurrencySymbol(loanOrder.getAssetCode());

		Map<String, String> extParam = loanOrder.getExtParam();
		Credit2ApplyAddRequest applyAddRequest = new Credit2ApplyAddRequest();
		applyAddRequest.setBorrowerId(loanOrder.getMemberId());
		applyAddRequest.setBorrowAmount(AmountScaleUtil.cal2Wexyun(loanOrder.getAmount()));
		applyAddRequest.setBorrowDuration(
				Integer.valueOf(extParam.get(LoanOrderExtParamKey.BORROW_DURATION)));
		applyAddRequest.setDurationType(DurationType.DAY);
		applyAddRequest.setExternalApplyId(String.valueOf(commandId));
		applyAddRequest.setReceiveMemberId(loanOrder.getMemberId());
		applyAddRequest.setLoanType(loanProduct.getLoanType());

		applyAddRequest.setSourceIdentity("dcc_app");
		applyAddRequest.setApplyDate(
				new Date(Long.valueOf(extParam.get(LoanOrderExtParamKey.APPLY_DATE))));

		applyAddRequest.setBorrowName(extParam.get(LoanOrderExtParamKey.BORROWER_NAME));
		applyAddRequest.setCertNo(extParam.get(LoanOrderExtParamKey.ID_CARD_NO));
		applyAddRequest.setBankCard(extParam.get(LoanOrderExtParamKey.BANK_CARD_NO));
		applyAddRequest.setMobile(extParam.get(LoanOrderExtParamKey.MOBILE));
		applyAddRequest.setBankMobile(extParam.get(LoanOrderExtParamKey.BANK_CARD_MOBILE));

		Map<String, String> itemDetailMap = new HashMap<>();
		itemDetailMap.put("agreementRepayMode", "TOGETHER");
		applyAddRequest.setItemDetailMap(itemDetailMap);

		applyAddRequest.setLoanMaterialList(buildLoanMaterialList(loanOrder));

		return applyAddRequest;
	}

	private AdvancedResult<AuditingOrder, AuditingOrderTrigger> successfulApplication(UnretryableCommand command, String applyId) {
		return new AdvancedResult<>(new TriggerBehavior<>(AuditingOrderTrigger.APPLY, order -> {
			order.setApplyId(applyId);
			unretryableCommandFunction.updateStatus(command, GeneralCommandStatus.SUCCESS.name());
		}), NextOperation.PAUSE);
	}

	private AdvancedResult<AuditingOrder, AuditingOrderTrigger> rejectApplication(
			UnretryableCommand command, BaseResponse response) {
		return new AdvancedResult<>(new TriggerBehavior<>(AuditingOrderTrigger.REJECT, order -> {
			order.setFailCode(response.getResponseCode());
			order.setFailMessage(response.getResponseMessage());
			unretryableCommandFunction.updateStatus(command, GeneralCommandStatus.FAILURE.name());
		}));
	}
}
