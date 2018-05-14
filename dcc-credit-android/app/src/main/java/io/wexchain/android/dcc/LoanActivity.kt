package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R

class LoanActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)
        setWindowExtended()
        initClicks()
    }

    private fun initClicks() {
        findViewById<View>(R.id.card_my_address).setOnClickListener {
            navigateTo(BeneficiaryAddressesManagementActivity::class.java)
        }
        findViewById<View>(R.id.card_start_loan).setOnClickListener {
            navigateTo(LoanProductListActivity::class.java)
        }
        findViewById<View>(R.id.card_my_loan).setOnClickListener {
            navigateTo(LoanRecordsActivity::class.java)
        }
        findViewById<View>(R.id.card_incentive_program).setOnClickListener {
            navigateTo(IncentiveProgramActivity::class.java)
        }
    }
}
