package io.wexchain.android.dcc.modules.repay

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.App
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.AssetsRepository
import io.wexchain.android.dcc.repo.db.CurrencyMeta
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.SharedPreferenceUtil
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityReviewRepayBinding
import io.wexchain.dccchainservice.domain.LoanRepaymentBill
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import java.io.Serializable
import java.math.BigDecimal

class ReviewRepayActivity : BindActivity<ActivityReviewRepayBinding>() {

    val assetsRepository: AssetsRepository = App.get().assetsRepository

    lateinit var currencyMeta: CurrencyMeta

    override val contentLayoutId: Int
        get() = R.layout.activity_review_repay

    private val loanOrderId
        get() = intent.getLongExtra(Extras.EXTRA_LOAN_CHAIN_ORDER_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        binding.btnConfirm.setOnClickListener {

            binding.bill?.let {

                var bill: LoanRepaymentBill = it
                // 待还金额为0
                if (bill.noPayAmount.compareTo(BigDecimal.ZERO) == 0) {
                    navigateTo(DoRepayActivity::class.java) {
                        putExtra(Extras.EXTRA_LOAN_REPAY_BILL, bill as Serializable)
                    }
                } else {
                    //
                    val current_eth = SharedPreferenceUtil.get(bill.repaymentAddress + "_" + Extras.NEEDSAVEPENDDING, Extras.SAVEDPENDDING) as? EthsTransaction

                    if (null == current_eth) {
                        navigateTo(LoanRepayActivity::class.java) {
                            putExtra(Extras.EXTRA_LOAN_REPAY_BILL, bill as Serializable)
                        }
                    } else {
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
                        val p = App.get().passportRepository.getCurrentPassport()!!

                        var agent = App.get().assetsRepository.getDigitalCurrencyAgent(digitalCurrency)

                        agent.getNonce(p.address).observeOn(AndroidSchedulers.mainThread()).subscribe({
                            if (it > current_eth.nonce) {
                                navigateTo(LoanRepayActivity::class.java) {
                                    putExtra(Extras.EXTRA_LOAN_REPAY_BILL, bill as Serializable)
                                }
                            } else {
                                navigateTo(RepayingActivity::class.java)
                            }
                        }, {
                            Pop.toast(it.message ?: "系统错误", this)
                        })
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        val id = loanOrderId
        if (id == -1L) {
            postOnMainThread {
                finish()
            }
        } else {
            getRepaymentBill(id)
        }
    }

    private fun getRepaymentBill(id: Long) {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getRepaymentBill(it, id)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    binding.bill = it

                    if (it.assetCode != "DCC" && it.assetCode != "ETH") {

                        Thread(Runnable {
                            currencyMeta = assetsRepository.getCurrencyMeta(it.assetCode)
                        }).start()
                    }


                }, {
                    Pop.toast(it.message ?: "系统错误", this)
                })
    }
}
