package io.wexchain.android.dcc.tools

import android.support.annotation.MainThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.android.common.constant.Extras
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.util.gweiTowei
import java.math.BigInteger

object TransHelper {
    var oLlastNounce = BigInteger("0")
    var localPendingNounce = BigInteger("0")
    var lastLocPendingNounce = BigInteger("0")

    @JvmStatic
    @MainThread
    fun savePublicChain(e: EthsTransaction) {
        App.get().assetsRepository.pushPendingTx(e)
        SharedPreferenceUtil.save(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING, e)
    }

    @JvmStatic
    fun afterTransSuc(scratch: EthsTransactionScratch?, txId: String?) {
        if (txId != null && scratch != null) {
            val e = EthsTransaction(
                    digitalCurrency = scratch.currency,
                    txId = txId,
                    from = scratch.from,
                    to = scratch.to,
                    nonce = scratch.nonce,
                    amount = scratch.currency.toIntExact(scratch.amount),
                    transactionType = EthsTransaction.TYPE_TRANSFER,
                    remarks = scratch.remarks,
                    time = -1L,
                    blockNumber = -1L,
                    gas = scratch.gasLimit,
                    gasUsed = BigInteger.ZERO,
                    gasPrice = gweiTowei(scratch.gasPrice),
                    status = EthsTransaction.Status.PENDING
            )
            App.get().assetsRepository.pushPendingTx(e)
            // 公链交易 持久化到本地
            if (scratch.currency.chain == Chain.publicEthChain) {
                SharedPreferenceUtil.save(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING, e)
                SharedPreferenceUtil.save(scratch.to + "_" + Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING, e)
            }
        }
    }

    @JvmStatic
    fun getLocalPenddingData(): EthsTransaction? {

        return SharedPreferenceUtil.get(
                Extras.NEEDSAVEPENDDING,
                Extras.SAVEDPENDDING
        ) as? EthsTransaction
        /*  if (null != ss&&) {
              getLastNounce()
          }
          return ss*/
    }

    @JvmStatic
    fun ispenddingitem(txid: String, b: Boolean): Boolean {
        if (!b) {
            val l = getLocalPenddingData()
            if (null != l) {
                return txid == l.txId
            }
        }
        return false
    }

    //
    @JvmStatic
    fun getLastNounce(): BigInteger {
        /*val agent = App.get().assetsRepository.getDigitalCurrencyAgent(tx.digitalCurrency)
        agent.getNonce(App.get().passportRepository.getCurrentPassport()!!.address).map {
            a=it
            a
        }.subscribe()*/
        App.get().publicRpc.getTransactionCount(
                App.get().passportRepository.getCurrentPassport()!!.address,
                "latest"
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    oLlastNounce = it
                }, { stackTrace(it) }
        )
        return oLlastNounce
    }

    //查接口 pending的是否还没上链成功
    @JvmStatic
    fun isStillPendding(): Boolean {
        val l = getLocalPenddingData()
        if (null == l) {
            return false
        } else {
            if (getLastNounce() > l.nonce) {
                return false
            }
            return true
        }
    }

    @JvmStatic
    fun isrealPendding(txid: String): Boolean {
        return ispenddingitem(txid, true) && isStillPendding()
    }
}
