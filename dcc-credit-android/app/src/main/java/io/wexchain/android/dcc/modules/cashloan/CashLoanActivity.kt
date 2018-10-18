package io.wexchain.android.dcc.modules.cashloan

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashloanBinding

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
    }

    private fun initView() {
        binding.countdownProgress.setCountdownTime(900)
        binding.countdownProgress.setPercent(80)
        binding.countdownProgress.startCountDownTime {  }
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
