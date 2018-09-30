package io.wexchain.android.dcc.modules.trans.activity

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.trans.vm.Private2PublicVm
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.Private2PublicConfirmDialogFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPrivate2PublicBinding
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.util.computeTransCount
import io.wexchain.digitalwallet.util.computeTransCountKeep2Number
import java.math.BigDecimal

class Private2PublicActivity : BindActivity<ActivityPrivate2PublicBinding>() {

    override val contentLayoutId: Int = R.layout.activity_private2_public

    private val dc get() = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency

    private var poundge: String = ""

    val txVm = Private2PublicVm()
    private var transcount = BigDecimal(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = getViewModel()

        val feeRate = intent.getStringExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE)

        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.queryExchangeCondition(it, assetCode = "DCC_JUZIX").observeOn(AndroidSchedulers.mainThread())

                }.subscribe({
                    binding.etTransCount.hint = "最小交易数量" + computeTransCount(it.minAmount.toBigDecimal()).toPlainString()

                    binding.etToAccountNum.setText(computeTransCountKeep2Number(it.minAmount.toBigDecimal().subtract(it.fixedFeeAmount.toBigDecimal())).toPlainString())

                    txVm.toAddress.set(it.originReceiverAddress)
                    //binding.etPoundge.setText(computeTransCount(it.fixedFeeAmount.toBigDecimal()).toPlainString())
                    txVm.minTrans.set(computeTransCount(it.minAmount.toBigDecimal()).toPlainString())
                    txVm.poundge.set(computeTransCount(it.fixedFeeAmount.toBigDecimal()).toPlainString())
                    poundge = computeTransCount(it.fixedFeeAmount.toBigDecimal()).toPlainString()

                }, {})

        binding.btAll.setOnClickListener {
            binding.etTransCount.setText(binding.tvPrivateCount.text)
            binding.etTransCount.setSelection(binding.tvPrivateCount.text.length)
        }

        binding.etTransCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val txt = s?.toString()
                if (txt == ".") {
                    binding.etTransCount.setText("0.")
                    binding.etTransCount.setSelection(2)
                    return
                }
                if (!txt.isNullOrEmpty()) {
                    val publicCount = binding.tvPrivateCount.text.toString()
                    val maxinp = BigDecimal(publicCount)
                    val inp = BigDecimal(txt)
                    val result = inp.compareTo(maxinp)
                    if (result == 1) {
                        binding.tvPrivateCount.visibility = View.INVISIBLE
                        binding.tvPrivateTips.text = getString(R.string.across_trans_public_tip)
                        binding.tvPrivateTips.setTextColor(Color.parseColor("#ED190F"))
                    } else {
                        hidTips()
                    }
                } else {
                    hidTips()
                }
            }
        })

        setupEvents(dc, feeRate)
    }

    private fun hidTips() {
        binding.tvPrivateCount.visibility = View.VISIBLE
        binding.tvPrivateTips.text = getString(R.string.across_trans_private_count)
        binding.tvPrivateTips.setTextColor(Color.parseColor("#404040"))
    }

    private fun setupEvents(dc: DigitalCurrency, feeRate: String?) {
        val observer = Observer<EthsTransactionScratch> {
            it?.let {
                showConfirmDialog(it, poundge)
            }
        }
        binding.tx = txVm.apply {
            ensureDigitalCurrency(dc, feeRate)
            this.txProceedEvent.observe(this@Private2PublicActivity, observer)
            this.inputNotSatisfiedEvent.observe(this@Private2PublicActivity, Observer {
                it?.let {
                    CustomDialog(this@Private2PublicActivity).apply {
                        textContent = it
                    }.assembleAndShow()
                }
            })
            this.dataInvalidatedEvent.observe(this@Private2PublicActivity, Observer {
                binding.executePendingBindings()
            })
        }
        /*binding.etGesLimit.setOnFocusChangeListener { v, hasFocus ->
            binding.tx?.updateGasLimit(hasFocus)
        }*/
        App.get().passportRepository.currPassport.observe(this, Observer {
            it?.let {
                binding.passport = it
            }
        })
    }

    private fun showConfirmDialog(ethsTransactionScratch: EthsTransactionScratch, poundge: String) {
        Private2PublicConfirmDialogFragment.create(ethsTransactionScratch, poundge)
                .show(supportFragmentManager, null)
    }

}
