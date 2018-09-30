package io.wexchain.android.dcc

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.modules.repay.LoanRecordsActivity
import io.wexchain.dcc.R

class LoanSubmitResultActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_submit_result)
        initToolbar(false)
        findViewById<View>(R.id.btn_loan_records).setOnClickListener {
            finishActivity(LoanProductDetailActivity::class.java)
            finishActivity(StartLoanActivity::class.java)
            navigateTo(LoanRecordsActivity::class.java)
        }
    }
}
