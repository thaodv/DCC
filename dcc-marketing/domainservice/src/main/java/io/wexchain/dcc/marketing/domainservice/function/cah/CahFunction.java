package io.wexchain.dcc.marketing.domainservice.function.cah;

import java.math.BigInteger;

import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.hosting.model.CryptoWallet;

/**
 * CahFunction
 *
 * @author zhengpeng
 */
public interface CahFunction {

	CryptoWallet createEthWallet();

	BigInteger getBalance(String address, String assetCode);

	BigInteger getJuzixDccBalance(String address);

	CryptoWallet prepareEthWallet(String address, String privateKey);

	TransferOrder redeem(String requestNo, BigInteger amount, String payerAddress, String receiverAddress);

	TransferOrder transfer(String requestNo, BigInteger amount, String payerAddress,
						   String receiverAddress, String assetCode);

	BigInteger getGasPrice(String assetsCode);

	TransferOrder transferDccJuzix(String requestNo, BigInteger amount, String payerAddress, String receiverAddress);

}
