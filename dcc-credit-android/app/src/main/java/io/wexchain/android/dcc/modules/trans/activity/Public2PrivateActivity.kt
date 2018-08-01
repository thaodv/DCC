package io.wexchain.android.dcc.modules.trans.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.trans.vm.Public2PrivateVm
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.Public2PrivateConfirmDialogFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPublic2PrivateBinding
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.util.computeTransCount

class Public2PrivateActivity : BindActivity<ActivityPublic2PrivateBinding>() {

    override val contentLayoutId: Int = R.layout.activity_public2_private

    private val dc get() = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency

    val txVm = Public2PrivateVm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()


        binding.vm = getViewModel()

        val feeRate = intent.getStringExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE)

        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.queryExchangeCondition(it, assetCode = "DCC").observeOn(AndroidSchedulers.mainThread())

                }.subscribe({
                    binding.etTransCount.hint = "最小交易数量" + computeTransCount(it.minAmount.toBigDecimal()).toPlainString()

                    txVm.toAddress.set(it.originReceiverAddress)

                    txVm.poundge.set(computeTransCount(it.minAmount.toBigDecimal()).toPlainString())

                }, {
                })

        binding.btAll.setOnClickListener {
            binding.etTransCount.setText(binding.tvPublicCount.text)
            binding.etTransCount.setSelection(binding.tvPublicCount.text.length)
        }

        setupEvents(dc, feeRate)

    }

    private fun setupEvents(dc: DigitalCurrency, feeRate: String?) {
        val observer = Observer<EthsTransactionScratch> {
            it?.let {
                showConfirmDialog(it)
            }
        }
        binding.tx = txVm.apply {
            ensureDigitalCurrency(dc, feeRate)
            this.txProceedEvent.observe(this@Public2PrivateActivity, observer)
            this.inputNotSatisfiedEvent.observe(this@Public2PrivateActivity, Observer {
                it?.let {
                    CustomDialog(this@Public2PrivateActivity).apply {
                        textContent = it
                    }.assembleAndShow()
                }
            })
            this.dataInvalidatedEvent.observe(this@Public2PrivateActivity, Observer {
                binding.executePendingBindings()
            })
        }
        binding.etGesLimit.setOnFocusChangeListener { v, hasFocus ->
            binding.tx?.updateGasLimit(hasFocus)
        }
        App.get().passportRepository.currPassport.observe(this, Observer {
            it?.let {
                binding.passport = it
            }
        })
    }

    private fun showConfirmDialog(ethsTransactionScratch: EthsTransactionScratch) {
        Public2PrivateConfirmDialogFragment.create(ethsTransactionScratch)
                .show(supportFragmentManager, null)
    }
}
