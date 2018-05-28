package io.wexchain.cryptoasset.loan.service.function.cah;

import io.wexchain.cryptoasset.hosting.frontier.model.TransferOrder;
import io.wexchain.cryptoasset.hosting.model.CryptoWallet;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * CahFunction
 *
 * @author zhengpeng
 */
public interface CahFunction {

    CryptoWallet createEthWallet();

    BigInteger getBalance(String address, String assetCode);

    CryptoWallet prepareEthWallet(String address, String privateKey);

    TransferOrder deliver(String requestNo, BigDecimal amount, String receiverAddress, String assetsCode);

    TransferOrder fuel(String requestNo, String receiverAddress, String assetsCode);

    TransferOrder collect(String requestNo, BigDecimal amount, String repayAddress, String assetsCode);

    BigInteger getGasPrice(String assetsCode);

}
