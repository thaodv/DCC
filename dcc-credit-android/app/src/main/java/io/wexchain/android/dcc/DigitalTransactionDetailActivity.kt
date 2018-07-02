package io.wexchain.android.dcc

import android.os.Bundle
import android.text.method.LinkMovementMethod
import io.wexchain.android.dcc.base.BindActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalTransactionDetailBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.EthsTransaction
import org.web3j.utils.Numeric
import java.math.BigInteger

class DigitalTransactionDetailActivity : BindActivity<ActivityDigitalTransactionDetailBinding>() {
    override val contentLayoutId: Int = R.layout.activity_digital_transaction_detail
    val assetsRepository: AssetsRepository=App.get().assetsRepository

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
        binding.tvCancel.setOnClickListener {
            val agent = assetsRepository.getDigitalCurrencyAgent(tx.digitalCurrency)
           // agent.sendTransferTransaction(tx.from,tx.to,tx.amount,tx.gasPrice+1,,)


        }
    }

    private fun updateInfoIfRequired(tx: EthsTransaction) {
        val agent = App.get().assetsRepository.getDigitalCurrencyAgent(tx.digitalCurrency)
        val gasUnknown = !tx.onPrivateChain() && (tx.gas == BigInteger.ZERO || tx.gasPrice == BigInteger.ZERO)
        if (tx.blockNumber == 0L || gasUnknown) {
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
