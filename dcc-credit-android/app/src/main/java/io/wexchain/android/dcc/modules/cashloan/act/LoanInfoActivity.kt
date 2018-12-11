package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanInfoBinding
import io.wexchain.ipfs.utils.io_main

class LoanInfoActivity : BindActivity<ActivityLoanInfoBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_loan_info

    val orderId by lazy {
        intent.getStringExtra(Extras.EXTRA_LOAN_RECORD_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        initData()
    }

    private fun initData() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getTNLoanOrderDetail(it, orderId)
                }
                .io_main()
                .withLoading()
                .subscribeBy {
                    binding.vm!!.order.set(it)
                }
    }

    private fun initVm() {
        binding.vm = getViewModel()
    }
}
