package io.wexchain.dcc.marketing.domainservice.function.cah.impl;

import java.math.BigInteger;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.wexchain.dcc.marketing.domainservice.function.cah.CahFunction;
import io.wexchain.dcc.marketing.domainservice.function.validator.Code2Exception;

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

	private final static String DCC_JUZIX_ASSET_CODE = "DCC_JUZIX";
	private final static int QUERY_TIMES = 10;
	private final static long WAIT_TIME = 1000L;

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
	public BigInteger getJuzixDccBalance(String address) {
		return getBalance(address, DCC_JUZIX_ASSET_CODE);
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
	public TransferOrder redeem(String requestNo, BigInteger amount, String payerAddress, String receiverAddress) {
		return transferDccJuzix(requestNo, amount, payerAddress, receiverAddress);
	}

	@Override
	public TransferOrder transferDccJuzix(String requestNo, BigInteger amount, String payerAddress,
			String receiverAddress) {

		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setRequestIdentity(new RequestIdentity("DCC-MARKETING", requestNo));
		transferRequest.setPayerAddress(payerAddress);
		transferRequest.setAssetCode("DCC_JUZIX");
		transferRequest.setReceiverAddress(receiverAddress);
		transferRequest.setAmount(amount);
		return transfer(transferRequest);
	}

	@Override
	public TransferOrder transfer(String requestNo, BigInteger amount, String payerAddress,
								  String receiverAddress, String assetCode) {

		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setRequestIdentity(new RequestIdentity("DCC-MARKETING", requestNo));
		transferRequest.setPayerAddress(payerAddress);
		transferRequest.setAssetCode(assetCode);
		transferRequest.setReceiverAddress(receiverAddress);
		transferRequest.setAmount(amount);
		return transfer(transferRequest);
	}

	@Override
	public BigInteger getGasPrice(String assetsCode) {
		return Code2Exception.handleResultResponse(transferFacade.getTransferFee(assetsCode));
	}

	private TransferOrder transfer(TransferRequest transferRequest) {
		ResultResponse<TransferOrder> transferResult = transferFacade.transfer(transferRequest);
		TransferOrder transferOrder = Code2Exception.handleResultResponse(transferResult);
		// 轮询
		return loopQueryTransferResult(transferOrder.getRequestIdentity());
	}

	private TransferOrder loopQueryTransferResult(RequestIdentity requestIdentity) {
		for (int i = 0; i < QUERY_TIMES; i++) {
			try {
				ResultResponse<TransferOrder> getOrdeResult = transferFacade.getTransferOrder(requestIdentity);
				TransferOrder transferOrder = Code2Exception.handleResultResponse(getOrdeResult);
				if (transferOrder.getStatus() != TransferOrderStatus.CREATED) {
					return transferOrder;
				}
				Thread.sleep(WAIT_TIME);
			} catch (Exception e) {
				logger.warn("Loop query transfer result fail", e);
			}
		}
		throw new ContextedRuntimeException("Can not get the event of update order");
	}
}
