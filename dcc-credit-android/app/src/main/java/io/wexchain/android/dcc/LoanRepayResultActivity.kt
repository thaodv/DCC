package io.wexchain.android.dcc

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R

class LoanRepayResultActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_repay_result)
        initToolbar(false)
        findViewById<View>(R.id.btn_to_records).setOnClickListener {
            navigateTo(LoanRecordsActivity::class.java)
        }
    }
}
