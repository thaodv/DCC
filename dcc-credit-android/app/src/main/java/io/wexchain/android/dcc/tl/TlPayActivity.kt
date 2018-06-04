package io.wexchain.android.dcc.tl

import com.xxy.maple.tllibrary.activity.TlWalletPayActivity
import com.xxy.maple.tllibrary.entity.SignParams
import io.wexchain.android.dcc.App
import org.web3j.crypto.Credentials
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.request.RawTransaction
import org.web3j.utils.Numeric
import java.math.BigInteger


class TlPayActivity:TlWalletPayActivity(){

    private val credentials:Credentials?
        get() = App.get().passportRepository.getCurrentPassport()?.credential

    override fun transmitSign(params: SignParams): String? {
        val c = credentials
        c ?:return null
        return signContractTrascation(c, params.nonce, params.gasPrice, params.to.toString(), params.gasLimit, params.value, params.dataHex)
    }

    fun signContractTrascation(
        credentials: Credentials,
        nonce: BigInteger,
        gasPrice: BigInteger,
        to: String,
        gasLimit: BigInteger,
        value: BigInteger,
        data: String
    ): String {
        val rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data)
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        return Numeric.toHexString(signedMessage)
    }

    companion object {

        const val ACTION_SIGN_TX = "io.wexchain.intent.action.SIGN_ETH_TRANSACTION_TL"
    }
}