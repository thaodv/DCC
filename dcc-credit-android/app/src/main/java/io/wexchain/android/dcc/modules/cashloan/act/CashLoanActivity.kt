package io.wexchain.android.dcc.modules.cashloan.act

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.cashloan.vm.CashLoanVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashloanBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.type.TnOrderStatus
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main

/**
 *Created by liuyang on 2018/10/15.
 */
class CashLoanActivity : BindActivity<ActivityCashloanBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_cashloan

    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getTnLastOrder(it)
                }
                .map {
                    orderId = it.orderId
                    it.status
                }
                .doMain()
                .doOnError {
                    if (it is DccChainServiceException) {
                        initVm(TnOrderStatus.NONE)
                    } else {
                        toast("系统错误")
                    }
                }
                .subscribeBy {
                    initVm(it)
                }
    }

    private fun initVm(status: TnOrderStatus) {
        binding.vm = getViewModel<CashLoanVm>()
                .apply {
                    loanStatus.set(status)
                }
        when (status) {
            TnOrderStatus.NONE, TnOrderStatus.REPAID -> getPageData()
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED, TnOrderStatus.AUDITED -> getPageData2()
            else -> {

            }
        }
        initClick(status)

    }

    private fun getPageData2() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getLoanCalculationInfo(it, orderId!!)
                }
                .io_main()
                .subscribeBy {

                }
    }

    private fun getPageData() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getMaximumAmount(it)
                }
                .io_main()
                .subscribeBy {
                    binding.vm?.maximumAmount?.set("¥$it")
                }
    }

    private fun initView() {
        binding.countdownProgress.setCountdownTime(900)
        binding.countdownProgress.setPercent(80)
        binding.countdownProgress.startCountDownTime { }
    }

    private fun initClick(status: TnOrderStatus) {
        binding.vm!!.apply {
            requestCall.observe(this@CashLoanActivity, Observer {
                when (status){
                    TnOrderStatus.NONE,TnOrderStatus.REPAID->{
                        navigateTo(CashCertificationActivity::class.java)
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_cash_loan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_my_cash_loan -> {
                navigateTo(CashLoanRecordsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
