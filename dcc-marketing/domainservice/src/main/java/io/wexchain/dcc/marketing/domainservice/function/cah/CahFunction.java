package io.wexchain.dcc.marketing.domainservice.function.cah;

import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.hosting.model.CryptoWallet;

import java.math.BigInteger;

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

    TransferOrder redeem(String requestNo, BigInteger amount, String payerAddress, String receiverAddress) ;

    BigInteger getGasPrice(String assetsCode);

}
