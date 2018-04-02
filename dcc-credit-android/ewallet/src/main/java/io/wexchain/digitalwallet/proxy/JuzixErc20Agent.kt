package io.wexchain.digitalwallet.proxy

import android.support.annotation.Size
import io.reactivex.Single
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransactionScratch
import org.spongycastle.jcajce.provider.digest.SHA3
import org.web3j.crypto.Credentials
import java.math.BigInteger
import java.util.*

/**
 * Created by sisel on 2018/2/7.
 */
class JuzixErc20Agent(
        erc20: DigitalCurrency,
        rpcAgent: EthsRpcAgent,
        txAgent: EthsTxAgent
) : Erc20Agent(erc20, rpcAgent, txAgent) {
    override fun getNonce(address: String): Single<BigInteger> {
        return Single.just(juzixChainNonce(address))
    }

    override fun encodeTransfer(nonce: BigInteger, from: Credentials, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, remarks: String?): String {
        return super.encodeTransfer(nonce, from, to, amount, GAS_PRICE, GAS_LIMIT, remarks)
    }

    override fun getGasLimit(ethsTransactionScratch: EthsTransactionScratch): Single<BigInteger> {
        return Single.just(GAS_LIMIT)
    }

    override fun getGasPrice(): Single<BigInteger> {
        return Single.just(GAS_PRICE)
    }

    companion object {

        val GAS_PRICE = BigInteger("21000000000")
        var GAS_LIMIT = BigInteger("200000000")

        fun juzixChainNonce(address: String, timestamp: Long = System.currentTimeMillis()): BigInteger {
            val byteArray = timestamp.toByteArray()
            val string = UUID.randomUUID().toString() + address
            val md = SHA3.DigestSHA3(256)
            md.update(string.toByteArray())
            val digest = md.digest()
            val nonce = ByteArray(32)
            System.arraycopy(byteArray, 0, nonce, 0, 8)
            System.arraycopy(digest, 0, nonce, 8, 24)
            return BigInteger(nonce)
        }

        @Size(8)
        fun Long.toByteArray(): ByteArray {
            var value = this
            val result = ByteArray(8)
            for (i in 7 downTo 0) {
                result[i] = (value and 0xffL).toByte()
                value = value shr 8
            }
            return result
        }
    }
}