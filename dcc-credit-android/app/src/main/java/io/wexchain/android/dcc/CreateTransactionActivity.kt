package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.wexmarket.android.passport.RequestCodes
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.TransactionConfirmDialogFragment
import io.wexchain.android.dcc.vm.TransactionVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityCreateTransactionBinding
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransactionScratch

class CreateTransactionActivity : BindActivity<ActivityCreateTransactionBinding>() {
    override val contentLayoutId: Int = R.layout.activity_create_transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        val dc = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY)!! as DigitalCurrency
        val feeRate = intent.getStringExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE)
        setupEvents(dc,feeRate)
        setupButtons()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                val txVm = binding.tx
                if (address != null && txVm != null) {
                    txVm.toAddress.set(address)
                    binding.executePendingBindings()
                    binding.etInputAddress.setSelection(address.length)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupButtons() {
        binding.ibScanAddress.setOnClickListener {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
            }, RequestCodes.SCAN)
        }
    }

    private fun setupEvents(dc: DigitalCurrency, feeRate: String?) {
        val observer = Observer<EthsTransactionScratch> {
            it?.let {
                showConfirmDialog(it)
            }
        }
        binding.tx = TransactionVm().apply {
            ensureDigitalCurrency(dc,feeRate)
            this.txProceedEvent.observe(this@CreateTransactionActivity, observer)
            this.inputNotSatisfiedEvent.observe(this@CreateTransactionActivity, Observer {
                it?.let {
                    CustomDialog(this@CreateTransactionActivity).apply {
                        textContent = it
                    }.assembleAndShow()
                }
            })
        }
        App.get().passportRepository.currPassport.observe(this, Observer {
            it?.let {
                binding.passport = it
            }
        })
    }

    private fun showConfirmDialog(ethsTransactionScratch: EthsTransactionScratch) {
        TransactionConfirmDialogFragment.create(ethsTransactionScratch)
                .show(supportFragmentManager, null)
    }
}
