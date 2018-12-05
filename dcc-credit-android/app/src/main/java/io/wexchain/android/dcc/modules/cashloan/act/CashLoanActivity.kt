package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashloanBinding
import io.wexchain.dccchainservice.type.TnOrderStatus

/**
 *Created by liuyang on 2018/10/15.
 */
class CashLoanActivity : BindActivity<ActivityCashloanBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_cashloan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
//        initView()
        initClick()
        initData()
    }

    private fun initData() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.createLoanOrder(it)
                }
                .map {
                    it.status ?: TnOrderStatus.NONE
                }


    }

    private fun initView() {
        binding.countdownProgress.setCountdownTime(900)
        binding.countdownProgress.setPercent(80)
        binding.countdownProgress.startCountDownTime { }
    }

    private fun initClick() {
        binding.cashLoanApply.onClick {
            navigateTo(CashCertificationActivity::class.java)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_cash_loan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_my_cash_loan -> {
                navigateTo(MyLoanActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
