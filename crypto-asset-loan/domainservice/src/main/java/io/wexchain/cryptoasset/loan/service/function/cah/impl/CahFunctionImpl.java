package io.wexchain.cryptoasset.loan.service.function.cah.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wexmarket.topia.commons.rpc.RequestIdentity;
import com.wexmarket.topia.commons.rpc.ResultResponse;

import io.wexchain.cryptoasset.hosting.constant.ChainCode;
import io.wexchain.cryptoasset.hosting.constant.TransferOrderStatus;
import io.wexchain.cryptoasset.hosting.frontier.BalanceFacade;
import io.wexchain.cryptoasset.hosting.frontier.BalanceRequest;
import io.wexchain.cryptoasset.hosting.frontier.TransferFacade;
import io.wexchain.cryptoasset.hosting.frontier.TransferRequest;
import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.hosting.model.CryptoWallet;
import io.wexchain.cryptoasset.hosting.wallet.CryptoWalletFacade;
import io.wexchain.cryptoasset.hosting.wallet.ImportWalletRequest;
import io.wexchain.cryptoasset.loan.common.function.Code2Exception;
import io.wexchain.cryptoasset.loan.service.function.cah.CahFunction;
import io.wexchain.cryptoasset.loan.service.util.AmountScaleUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * CahFunctionImpl
 *
 * @author zhengpeng
 */
@Service
public class CahFunctionImpl implements CahFunction {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CryptoWalletFacade cryptoWalletFacade;

	@Autowired
	private TransferFacade transferFacade;

	@Autowired
	private BalanceFacade balanceFacade;

	@Value("${wallet.address.collect}")
	private String collectAddress;

	@Value("${fuel.gas.limit.eth}")
	private Integer ethGasLimit;

	@Value("${fuel.gas.limit.token}")
	private Integer tokenGasLimit;

	@Value("${fuel.gas.price}")
	private BigInteger gasPrice;

	@Value("${wallet.address.pay}")
	private String payerAddress;

	@Override
	public CryptoWallet createEthWallet() {
		ResultResponse<CryptoWallet> createResult = cryptoWalletFacade.createWallet(ChainCode.ETH.name());
		return Code2Exception.handleResultResponse(createResult);
	}

	@Override
	public BigInteger getBalance(String address, String assetCode) {
		BalanceRequest balanceRequest = new BalanceRequest();
		balanceRequest.setAssetCode(assetCode);
		balanceRequest.setOwnerAddress(address);
		return Code2Exception.handleResultResponse(balanceFacade.getBalance(balanceRequest));
	}

	@Override
	public CryptoWallet prepareEthWallet(String address, String privateKey) {
		ResultResponse<CryptoWallet> getResult = cryptoWalletFacade.getWallet(address);
		CryptoWallet cryptoWallet = Code2Exception.handleResultResponse(getResult);
		if (cryptoWallet == null) {
			ImportWalletRequest importWalletRequest = new ImportWalletRequest();
			importWalletRequest.setAddress(address);
			importWalletRequest.setPrivateKey(privateKey);
			importWalletRequest.setChainCode(ChainCode.ETH.name());
			ResultResponse<CryptoWallet> importResult = cryptoWalletFacade.importWallet(importWalletRequest);
			cryptoWallet = Code2Exception.handleResultResponse(importResult);
		}
		return cryptoWallet;
	}

	@Override
	public TransferOrder deliver(String requestNo, BigDecimal amount, String receiverAddress, String assetsCode) {

		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setRequestIdentity(new RequestIdentity("CAL", requestNo));
		transferRequest.setPayerAddress(payerAddress);
		transferRequest.setAssetCode(assetsCode);
		transferRequest.setReceiverAddress(receiverAddress);
		transferRequest.setAmount(AmountScaleUtil.cal2Cah(amount));
		return transfer(transferRequest);
	}

	@Override
	public TransferOrder fuel(String requestNo, String receiverAddress, String assetsCode) {
		BigInteger gasPrice = getGasPrice(assetsCode);

		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setRequestIdentity(new RequestIdentity("CAL", requestNo));
		transferRequest.setAssetCode("ETH");
		transferRequest.setPayerAddress(payerAddress);
		transferRequest.setReceiverAddress(receiverAddress);
		transferRequest.setAmount(gasPrice);
		return transfer(transferRequest);
	}

	@Override
	public BigInteger getGasPrice(String assetsCode) {
		return Code2Exception.handleResultResponse(transferFacade.getTransferFee(assetsCode));
	}


	@Override
	public TransferOrder collect(String requestNo, BigDecimal amount, String repayAddress, String assetsCode) {
		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setRequestIdentity(new RequestIdentity("CAL", requestNo));
		transferRequest.setAssetCode(assetsCode);
		transferRequest.setPayerAddress(repayAddress);
		transferRequest.setReceiverAddress(collectAddress);
		transferRequest.setAmount(AmountScaleUtil.cal2Cah(amount));
		return transfer(transferRequest);
	}

	private TransferOrder transfer(TransferRequest transferRequest) {
		ResultResponse<TransferOrder> transferResult = transferFacade.transfer(transferRequest);
		TransferOrder transferOrder = Code2Exception.handleResultResponse(transferResult);
		// 轮询
		return loopQueryTransferResult(transferOrder.getRequestIdentity());
	}

	private TransferOrder loopQueryTransferResult(RequestIdentity requestIdentity) {
		for (int i = 0; i < 60; i++) {
			try {
				ResultResponse<TransferOrder> getOrdeResult = transferFacade.getTransferOrder(requestIdentity);
				TransferOrder transferOrder = Code2Exception.handleResultResponse(getOrdeResult);
				if (transferOrder.getStatus() != TransferOrderStatus.CREATED) {
					return transferOrder;
				}
				Thread.sleep(10000L);
			} catch (Exception e) {
				logger.warn("Loop query transfer result fail", e);
			}
		}
		throw new ContextedRuntimeException("Can not get the event of update order");
	}
}
