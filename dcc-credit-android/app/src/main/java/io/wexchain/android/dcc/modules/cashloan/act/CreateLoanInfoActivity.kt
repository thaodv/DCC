package io.wexchain.android.dcc.modules.cashloan.act

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCreateloaninfoBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.io_main
import java.math.BigDecimal

/**
 *Created by liuyang on 2018/12/6.
 */
class CreateLoanInfoActivity : BindActivity<ActivityCreateloaninfoBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_createloaninfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initData()
        initClick()
    }

    private fun initClick() {
        binding.vm!!.apply {
            confirmLoan.observe(this@CreateLoanInfoActivity, Observer {
                doSubmitLoan()
            })

            showAgreement.observe(this@CreateLoanInfoActivity, Observer {

            })

            showbankCard.observe(this@CreateLoanInfoActivity, Observer {
                navigateTo(CashBoundBankActivity::class.java)
            })

        }
    }

    private fun doSubmitLoan() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.confirmLoan(it, 0L, 0L, true, BigDecimal(0))
                }
                .io_main()
                .withLoading()
                .subscribeBy {

                }
    }

    private fun initData() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getLoanCalculationInfo(it, "")
                }
                .zipWith(App.get().scfApi.getBindingBankCard())
                .io_main()
                .withLoading()
                .subscribeBy {
                    val result = it.second
                    if (result.isSuccess) {
                        result.result?.let {
                            binding.vm!!.bankCardNum.set("尾号${it.bankCardNo}")
                        }
                    }

                }

    }
}