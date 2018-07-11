package io.wexchain.android.dcc

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.tools.TransHelper
import io.wexchain.android.dcc.tools.pair
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalTransactionDetailBinding
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.util.computeEthTxFeebyW
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

class DigitalTransactionDetailActivity : BindActivity<ActivityDigitalTransactionDetailBinding>() {
    override val contentLayoutId: Int = R.layout.activity_digital_transaction_detail
    val assetsRepository: AssetsRepository = App.get().assetsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        val address = App.get().passportRepository.currPassport.value!!.address
        val tx = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_TRANSACTION)!! as EthsTransaction
        binding.tx = tx
        binding.owner = address
        if (!tx.onPrivateChain()) {
            binding.tvTransactionIdValue.movementMethod = LinkMovementMethod.getInstance()
        }
        updateInfoIfRequired(tx)
        binding.btnToCancle.setOnClickListener {
            val agent = assetsRepository.getDigitalCurrencyAgent(tx.digitalCurrency)
            val cancelGasPrice: BigInteger = tx.gasPrice
            val scratch = EthsTransactionScratch(
                currency = tx.digitalCurrency,
                from = tx.from,
                to = tx.to,
                amount = tx.amount.toBigDecimal(),
                gasPrice = BigDecimal.ZERO,
                gasLimit = BigInteger.ZERO,
                remarks = tx.remarks
            )
            Single
                .zip(
                    agent.getGasLimit(scratch)
                        .onErrorReturn { BigInteger.valueOf(100000) },
                    agent.getGasPrice(),
                    pair()
                )
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    { (gasLimit, gasprice) ->
                        val fpp = maxOf(
                            gasprice,
                            cancelGasPrice + BigDecimal("0.5").scaleByPowerOfTen(9).toBigInteger()
                        )
                        val ss = computeEthTxFeebyW(gasLimit, gasprice)
                        CustomDialog(this)
                            .apply {
                                title = "提示"
                                textContent = "撤销后的交易记录将覆盖原交易记录（nonce=" + tx.nonce + "）。撤销交易需消耗" +
                                        ss +
                                        "ETH。确认撤销此交易吗？\n"
                                withPositiveButton("确定") {
                                    agent.editTransferTransaction(
                                        Single.just(tx.nonce),
                                        App.get().passportRepository.getCurrentPassport()!!.credential,
                                        address,
                                        BigInteger("1"),
                                        fpp,
                                        gasLimit
                                    ).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                        {
                                            //  AlertDialog.
                                            toast("请求成功")
                                            var ss = scratch
                                            ss.nonce = tx.nonce
                                            ss.amount = ss.currency.toDecimalAmount(BigInteger("1"))
                                            ss.to = address
                                            ss.gasPrice = fpp.toBigDecimal()
                                            ss.gasLimit = gasLimit
                                            TransHelper.afterTransSuc(scratch, it.second)
                                            finish()

                                        }, {
                                            toast("请求失败")
                                            stackTrace(it)
                                            finish()
                                        }
                                    )
                                    true
                                }
                                withNegativeButton("取消")
                            }
                            .assembleAndShow()
                    }, {

                    }
                )
            /*  .flatMap { (gasLimit, gasprice) ->
          val fpp= maxOf(gasprice,cancelGasPrice+BigDecimal("0.5").scaleByPowerOfTen(9).toBigInteger())
          val ss=computeEthTxFeebyW(gasLimit,gasprice)
          agent.sendTransferTransaction(App.get().passportRepository.getCurrentPassport()!!.credential,tx.to,tx.amount,fpp,gasLimit)
      }.subscribe(

      )*/


            /*agent.getGasLimit(scratch).observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn { BigInteger.valueOf(100000) }
                    .flatMap {gaslimit->
                        agent.getGasPrice()
                                .flatMap {
                                    Single.just(gaslimit to it)
                                }
                    }
                    .subscribe({
                    agent.sendTransferTransaction(App.get().passportRepository.getCurrentPassport()!!.credential,tx.to,tx.amount,cancelGasPrice,it.first)},{
                        stackTrace(it)
                    }
            )*/
            //tx.txId


        }
        binding.btnToEdit.setOnClickListener {
            CustomDialog(this)
                .apply {
                    title = "提示"
                    textContent = "撤销后的交易记录将覆盖原交易记录（nonce=" + tx.nonce + "）。" +
                            "确认编辑此交易吗？\n"
                    withPositiveButton("确定") {
                       // navigateTo(CreateTransactionActivity::class.java)
                        startActivityForResult(Intent(this@DigitalTransactionDetailActivity, CreateTransactionActivity::class.java).apply {
                            putExtra(Extras.EXTRA_EDIT_TRANSACTION, tx)
                        },RequestCodes.Edit_TRANSACTION)
                        true
                    }
                    withNegativeButton("取消")
                }
                .assembleAndShow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.Edit_TRANSACTION-> {
              finish()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun updateInfoIfRequired(tx: EthsTransaction) {
        val agent = App.get().assetsRepository.getDigitalCurrencyAgent(tx.digitalCurrency)
        val gasUnknown =
            !tx.onPrivateChain() && (tx.gas == BigInteger.ZERO || tx.gasPrice == BigInteger.ZERO)
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
