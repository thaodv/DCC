package io.wexchain.android.dcc.modules.repay

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

class LoanRepayResultActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_repay_result)
        initToolbar(false)
        findViewById<View>(R.id.btn_to_records).setOnClickListener {
            finishActivity(ReviewRepayActivity::class.java)
            finishActivity(LoanRecordDetailActivity::class.java)
            finish()
        }
    }
}
