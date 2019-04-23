package io.wexchain.android.dcc.modules.trans.activity

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.onClick
import io.wexchain.android.common.tools.EditInputFilter
import io.wexchain.android.common.transTips
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.digestpocket.ChooseDigestActivity
import io.wexchain.android.dcc.modules.other.QrScannerActivity
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.repo.db.TransRecord
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.TransactionConfirmDialogFragment
import io.wexchain.android.dcc.vm.TransactionVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCreateTransactionBinding
import io.wexchain.digitalwallet.*

class CreateTransactionActivity : BindActivity<ActivityCreateTransactionBinding>() {

    override val contentLayoutId: Int = R.layout.activity_create_transaction
    var isEdit = false//是否是编辑转账
    val txVm = TransactionVm()

    private val addr get() = intent.getSerializableExtra(Extras.EXTRA_SELECT_ADDRESS) as? AddressBook
    private val trustAddress get() = intent?.getStringExtra("address")
    private val transRecord get() = intent.getSerializableExtra(Extras.EXTRA_SELECT_TRANSRECORD) as? TransRecord
    private val money get() = intent.getStringExtra(Extras.EXTRA_PAY_MONEY)
    private val isRepayment get() = intent.getIntExtra("is_repayment", 0)
    private val mUse get() = intent?.getStringExtra("use")

    val feeRate = intent?.getStringExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE)

    private var dc: DigitalCurrency? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        dc = intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as? DigitalCurrency

        binding.rlChoose.onClick {

            startActivityForResult(
                    Intent(this, ChooseDigestActivity::class.java),
                    RequestCodes.CHOOSE_DIGEST_COIN
            )
        }

        val tx = intent.getSerializableExtra(Extras.EXTRA_EDIT_TRANSACTION) as? EthsTransaction

        if (null != dc) {
            if (dc!!.chain == Chain.JUZIX_PRIVATE) {
                binding.rlChoose.visibility = View.GONE
            }
            initParameters(dc!!)
        }

        if (null != mUse) {
            binding.rlChoose.visibility = View.GONE
        }

        if (null != tx) {
            isEdit = true
            txVm.isEdit = true
            txVm.tx = tx
            dc = tx.digitalCurrency

            initParameters(dc!!)

            binding.etInputAmount.filters  = arrayOf<InputFilter>(EditInputFilter(dc!!.decimals))

            binding.etInputAmount.setText(tx.digitalCurrency.toDecimalAmount(tx.amount).setSelfScale(4))
            txVm.toAddress.set(tx.to)
            txVm.amount.set(tx.digitalCurrency.toDecimalAmount(tx.amount).setSelfScale(4))
            //   binding.etInputAddress.setText(tx.to)
            /*  binding.etInputGasPrice.setText(tx.digitalCurrency.toDecimalAmount(tx.gasPrice).currencyToDisplayStr())
           txVm.gasPrice.set(tx.digitalCurrency.toDecimalAmount(tx.gasPrice).currencyToDisplayStr())*/
            binding.etInputGasLimit.setText("" + tx.gas)
            txVm.gasLimit.set("" + tx.gas)

        }

        setupButtons()

        if (null != addr) {
            txVm.toAddress.set(addr!!.address)
        }
        if (null != transRecord) {
            txVm.toAddress.set(transRecord!!.address)
        }

        if (null != money) {
            txVm.amount.set(money)
        }

        if (null != trustAddress) {
            txVm.toAddress.set(trustAddress)

            binding.tx = txVm.apply {  }
        }

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
            RequestCodes.CHOOSE_DIGEST_COIN -> {

                if (resultCode == ResultCodes.RESULT_OK) {
                    dc = data?.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY) as DigitalCurrency

                    if ("DCC" == dc!!.symbol) {
                        dc = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }
                    }

                    initParameters(dc!!)
                    /*if (txVm != null && ba != null) {
                        txVm.toAddress.set(ba.address)
                        binding.executePendingBindings()
                        binding.etInputAddress.setSelection(ba.address.length)
                    }*/
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initParameters(dc: DigitalCurrency) {
        binding.name = dc.symbol
        binding.url = if (null == dc.icon) "" else dc.icon

        title = ("${dc.symbol} " + getString(R.string.transfer))

        binding.etInputAmount.filters  = arrayOf<InputFilter>(EditInputFilter(dc.decimals))

        binding.tvTransSymbol.text = dc.symbol
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

        setupEvents(dc, feeRate)
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
        TransactionConfirmDialogFragment.create(ethsTransactionScratch, isEdit, isRepayment = isRepayment)
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
