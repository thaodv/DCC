package io.wexchain.android.dcc.modules.cashloan.act

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashboundbankBinding

/**
 *Created by liuyang on 2018/12/6.
 */
class CashBoundBankActivity : BindActivity<ActivityCashboundbankBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_cashboundbank

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
    }

    private fun initVm() {
        binding.vm = getViewModel()
        binding.vm!!.apply {
            vCode.observe(this@CashBoundBankActivity, Observer {

            })
            toast.observe(this@CashBoundBankActivity, Observer {
                it?.let {
                    toast(it)
                }
            })
            confirm.observe(this@CashBoundBankActivity, Observer {

            })
        }
    }
}