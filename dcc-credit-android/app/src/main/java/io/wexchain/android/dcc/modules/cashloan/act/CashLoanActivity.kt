package io.wexchain.android.dcc.modules.cashloan.act

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.cashloan.vm.CashLoanVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashloanBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.type.TnOrderStatus
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
                .flatMap { order ->
                    orderId = order.id
                    if (TnOrderStatus.REJECTED == order.status) {
                        ScfOperations
                                .withScfTokenInCurrentPassport {
                                    App.get().scfApi.getAuditResult(it, orderId!!)
                                }
                                .map {
                                    if (it.canLoanTime.isNullOrEmpty()) {
                                        TnOrderStatus.NONE
                                    } else {
                                        TnOrderStatus.REJECTED
                                    }
                                }
                    } else {
                        Single.just(order.status)
                    }
                }
                .io_main()
                .doOnError {
                    if (it is DccChainServiceException) {
                        if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                            initVm(TnOrderStatus.NONE)
                        } else {
                            toast(it.message ?: "系统错误")
                        }
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
            TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.CREATED -> getPageData()
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED, TnOrderStatus.AUDITED -> getPageData2()
            TnOrderStatus.AUDITING -> initView()
            TnOrderStatus.REJECTED -> errorOrder()
            else -> {

            }
        }
        initClick(status)

    }

    private fun errorOrder() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getAuditResult(it, orderId!!)
                }
                .io_main()
                .subscribeBy {

                }

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
        binding.countdownProgress.setPercent(35)
        binding.countdownProgress.startCountDownTime { }
    }

    private fun initClick(status: TnOrderStatus) {
        binding.vm!!.apply {
            requestCall.observe(this@CashLoanActivity, Observer {
                when (status) {
                    TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.CREATED -> {
                        navigateTo(CashCertificationActivity::class.java)
                    }
                    TnOrderStatus.AUDITED -> {
                        navigateTo(CreateLoanInfoActivity::class.java)
                    }
                    TnOrderStatus.AUDITING -> {
                        toast("请耐心等待订单审核完成")
                    }
                }
            })
            loanTipsCall.observe(this@CashLoanActivity, Observer {
                toast("借款协议")
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
