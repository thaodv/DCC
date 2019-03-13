package io.wexchain.android.dcc.modules.repay

import android.content.ClipData
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.trans.activity.CreateTransactionActivity
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.repo.db.CurrencyMeta
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.android.dcc.view.dialog.WaitTransDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanRepayBinding
import io.wexchain.dccchainservice.domain.LoanRepaymentBill
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import java.io.Serializable

class LoanRepayActivity : BindActivity<ActivityLoanRepayBinding>() {

    val assetsRepository: AssetsRepository = App.get().assetsRepository

    lateinit var currencyMeta: CurrencyMeta

    override val contentLayoutId: Int
        get() = R.layout.activity_loan_repay

    private val repayBill
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_REPAY_BILL) as? LoanRepaymentBill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initData()

        binding.btnConfirmRepay.setOnClickListener {
            binding.bill?.let {

                var bill_temp = it

                val digitalCurrency: DigitalCurrency = when {
                    it.assetCode == "DCC" -> {
                        MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }

                    }
                    it.assetCode == "ETH" -> {
                        Currencies.Ethereum
                    }
                    else -> {
                        currencyMeta.toDigitalCurrency()
                    }
                }

                val eth = SharedPreferenceUtil.get(Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING) as? EthsTransaction

                if (null == eth) {
                    navigateTo(CreateTransactionActivity::class.java) {
                        putExtra(Extras.EXTRA_DIGITAL_CURRENCY, digitalCurrency)
                        putExtra(Extras.EXTRA_SELECT_ADDRESS, AddressBook(it.repaymentAddress, "") as Serializable)
                        putExtra(Extras.EXTRA_PAY_MONEY, it.noPayAmount.toString())
                        putExtra("is_repayment", 1)
                    }
                } else {
                    val p = App.get().passportRepository.getCurrentPassport()!!

                    var agent = App.get().assetsRepository.getDigitalCurrencyAgent(digitalCurrency)

                    agent.getNonce(p.address).observeOn(AndroidSchedulers.mainThread()).subscribe({
                        if (it > eth.nonce) {
                            navigateTo(CreateTransactionActivity::class.java) {
                                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, digitalCurrency)
                                putExtra(Extras.EXTRA_SELECT_ADDRESS, AddressBook(bill_temp.repaymentAddress, "") as Serializable)
                                putExtra(Extras.EXTRA_PAY_MONEY, bill_temp.noPayAmount.toString())
                                putExtra("is_repayment", 1)
                            }
                        } else {
                            val waitTransDialog = WaitTransDialog(this)
                            waitTransDialog.mTvText.text = "还币或转账正在进行中，请稍后..."
                            waitTransDialog.show()
                        }
                    }, {
                        Pop.toast(it.message ?: "系统错误", this)
                    })
                }
            }
        }
        binding.btnCopyAddress.setOnClickListener {
            val addr = binding.etRepayAddress.text?.toString()
            addr?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("还币地址", addr)
                toast("已复制到剪贴板")
            }
        }
    }

    private fun initData() {
        val bill = repayBill
        if (bill == null) {
            runOnUiThread { finish() }
        } else {
            binding.bill = bill

            if (bill.assetCode != "DCC" && bill.assetCode != "ETH") {

                Thread(Runnable {
                    currencyMeta = assetsRepository.getCurrencyMeta(bill.assetCode)
                }).start()
            }

        }
    }
}
