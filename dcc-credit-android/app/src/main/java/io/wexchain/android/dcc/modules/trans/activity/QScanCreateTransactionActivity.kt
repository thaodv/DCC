package io.wexchain.android.dcc.modules.trans.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.digestpocket.ChooseDigestActivity
import io.wexchain.android.dcc.modules.other.QrScannerActivity
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.TransactionConfirmDialogFragment
import io.wexchain.android.dcc.vm.TransactionVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityQscanCreateTransactionBinding
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransactionScratch

class QScanCreateTransactionActivity : BindActivity<ActivityQscanCreateTransactionBinding>() {

    override val contentLayoutId: Int = R.layout.activity_qscan_create_transaction
    val txVm = TransactionVm()

    /*private val trustAddress get() = intent.getStringExtra("address")
    private val money get() = intent.getStringExtra(Extras.EXTRA_PAY_MONEY)
    private val isRepayment get() = intent.getIntExtra("is_repayment", 0)*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.rlChoose.onClick {
            navigateTo(ChooseDigestActivity::class.java)
        }


        /*var dc = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as? DigitalCurrency
        val feeRate = intent.getStringExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE)

        binding.tvTransSymbol.text = dc!!.symbol
        App.get().assetsRepository.getBalance(dc, App.get().passportRepository.currPassport.value!!.address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    binding.btAll.isClickable = true
                    binding.etInputAmount.isEnabled = true
                    val balance = ViewModelHelper.getBalanceStr(dc, it)

                    binding.tvTransCount.text = if (balance == "--") "0.0000" else balance
                    binding.etInputAmount.transTips(binding.tvTransCount,
                            showTips = ::showTips,
                            hidTips = ::hidTips)
                }

        title = ("${dc.symbol} " + getString(R.string.transfer))
        setupEvents(dc, feeRate)
        setupButtons()

        if (null != money) {
            txVm.amount.set(money)
        }

        if (null != trustAddress) {
            txVm.toAddress.set(trustAddress)
        }*/

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
                    val ba = data?.getSerializableExtra(Extras.EXTRA_SELECT_ADDRESS) as? AddressBook
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
                    Intent(this, AddressBookActivity::class.java).putExtra("usage", 1),
                    RequestCodes.CHOOSE_BENEFICIARY_ADDRESS
            )
        }
        binding.btAll.onClick {
            binding.etInputAmount.setText(binding.tvTransCount.text)
            binding.etInputAmount.setSelection(binding.tvTransCount.text.length)
        }

    }


    private fun showTips() {
        binding.tvTransCount.visibility = View.INVISIBLE
        binding.tvTransSymbol.visibility = View.INVISIBLE
        binding.tvTransTips.text = getString(R.string.across_trans_tip1)
        binding.tvTransTips.setTextColor(Color.parseColor("#ED190F"))
    }

    private fun hidTips() {
        binding.tvTransCount.visibility = View.VISIBLE
        binding.tvTransSymbol.visibility = View.VISIBLE
        binding.tvTransTips.text = getString(R.string.across_trans_count1)
        binding.tvTransTips.setTextColor(Color.parseColor("#404040"))
    }

    private fun setupEvents(dc: DigitalCurrency, feeRate: String?) {
        val observer = Observer<EthsTransactionScratch> {
            it?.let {
                showConfirmDialog(it)
            }
        }
        binding.tx = txVm.apply {
            ensureDigitalCurrency(dc, feeRate)
            this.txProceedEvent.observe(this@QScanCreateTransactionActivity, observer)
            this.inputNotSatisfiedEvent.observe(this@QScanCreateTransactionActivity, Observer {
                it?.let {
                    CustomDialog(this@QScanCreateTransactionActivity).apply {
                        textContent = it
                    }.assembleAndShow()
                }
            })
            this.dataInvalidatedEvent.observe(this@QScanCreateTransactionActivity, Observer {
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
        TransactionConfirmDialogFragment.create(ethsTransactionScratch)
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
