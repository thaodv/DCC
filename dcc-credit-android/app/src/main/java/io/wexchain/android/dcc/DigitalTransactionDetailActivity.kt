package io.wexchain.android.dcc

import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.wexmarket.android.passport.base.BindActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityDigitalTransactionDetailBinding
import io.wexchain.digitalwallet.EthsTransaction
import org.web3j.utils.Numeric
import java.math.BigInteger

class DigitalTransactionDetailActivity : BindActivity<ActivityDigitalTransactionDetailBinding>() {
    override val contentLayoutId: Int = R.layout.activity_digital_transaction_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        val address = App.get().passportRepository.currPassport.value!!.address
        val tx = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_TRANSACTION)!! as EthsTransaction
        binding.tx = tx
        binding.owner = address
        if(!tx.onPrivateChain()) {
            binding.tvTransactionIdValue.movementMethod = LinkMovementMethod.getInstance()
        }
        updateInfoIfRequired(tx)
    }

    private fun updateInfoIfRequired(tx: EthsTransaction) {
        val agent = App.get().assetsRepository.getDigitalCurrencyAgent(tx.digitalCurrency)
        if (tx.blockNumber == 0L ||
                tx.gas == BigInteger.ZERO ||
                tx.gasPrice == BigInteger.ZERO) {
            agent.transactionByHash(tx.txId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tx.blockNumber = Numeric.toBigInt(it.blockNumber).toLong()
                        tx.gas = Numeric.toBigInt(it.gas)
                        tx.gasPrice = Numeric.toBigInt(it.gasPrice)
                        binding.tx = tx
                    }, {
                        stackTrace(it)
                    })
        }
        if (tx.gasUsed == BigInteger.ZERO) {
            agent.transactionReceipt(tx.txId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tx.gasUsed = Numeric.toBigInt(it.gasUsed)
                        binding.tx = tx
                    }, {
                        stackTrace(it)
                    })
        }
    }
}
