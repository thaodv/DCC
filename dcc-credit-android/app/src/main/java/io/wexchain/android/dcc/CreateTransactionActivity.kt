package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.wexmarket.android.passport.ResultCodes
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.TransactionConfirmDialogFragment
import io.wexchain.android.dcc.vm.TransactionVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCreateTransactionBinding
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.EthsTransactionScratch

class CreateTransactionActivity : BindActivity<ActivityCreateTransactionBinding>() {
    override val contentLayoutId: Int = R.layout.activity_create_transaction
    var isEdit = false//是否是编辑转账
    val txVm = TransactionVm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        var dc = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as? DigitalCurrency
        val feeRate = intent.getStringExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE)

        val tx = intent.getSerializableExtra(Extras.EXTRA_EDIT_TRANSACTION) as? EthsTransaction
        if (null != tx) {
            isEdit = true
            txVm.isEdit = true
            txVm.tx = tx
            dc = tx.digitalCurrency
            binding.etInputAmount.setText(dc.toDecimalAmount(tx.amount).toString())
            txVm.toAddress.set(tx.to)
            //   binding.etInputAddress.setText(tx.to)
            //   binding.etInputGasPrice.setText(dc.toDecimalAmount(tx.gasPrice).toString())
        }
        title = ("${dc!!.symbol} 转账")
        setupEvents(dc, feeRate)
        setupButtons()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val txVm = binding.tx
        when (requestCode) {
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null && txVm != null) {
                    txVm.toAddress.set(address)
                    binding.executePendingBindings()
                    binding.etInputAddress.setSelection(address.length)
                }
            }
            RequestCodes.CHOOSE_BENEFICIARY_ADDRESS -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    val ba = data?.getSerializableExtra(Extras.EXTRA_SELECT_ADDRESS) as? BeneficiaryAddress
                    if (txVm != null && ba != null) {
                        txVm.toAddress.set(ba.address)
                        binding.executePendingBindings()
                        binding.etInputAddress.setSelection(ba.address.length)
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setupButtons() {
        binding.ibChooseAddress.setOnClickListener {
            startActivityForResult(
                    Intent(this, BeneficiaryAddressesManagementActivity::class.java).putExtra("usage", 1),
                    RequestCodes.CHOOSE_BENEFICIARY_ADDRESS
            )
        }
    }

    private fun setupEvents(dc: DigitalCurrency, feeRate: String?) {
        val observer = Observer<EthsTransactionScratch> {
            it?.let {
                showConfirmDialog(it)
            }
        }
        binding.tx = txVm.apply {
            ensureDigitalCurrency(dc, feeRate)
            this.txProceedEvent.observe(this@CreateTransactionActivity, observer)
            this.inputNotSatisfiedEvent.observe(this@CreateTransactionActivity, Observer {
                it?.let {
                    CustomDialog(this@CreateTransactionActivity).apply {
                        textContent = it
                    }.assembleAndShow()
                }
            })
            this.dataInvalidatedEvent.observe(this@CreateTransactionActivity, Observer {
                binding.executePendingBindings()
            })
        }
        binding.etInputGasLimit.setOnFocusChangeListener { v, hasFocus ->
            binding.tx?.updateGasLimit(hasFocus)
        }
        App.get().passportRepository.currPassport.observe(this, Observer {
            it?.let {
                binding.passport = it
            }
        })
    }

    private fun showConfirmDialog(ethsTransactionScratch: EthsTransactionScratch) {
        TransactionConfirmDialogFragment.create(ethsTransactionScratch, isEdit)
                .show(supportFragmentManager, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_asset_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_asset_scan -> {
                startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                    putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
                }, RequestCodes.SCAN)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
