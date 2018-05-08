package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanRecordDetailBinding
import io.wexchain.dccchainservice.domain.LoanRecord

class LoanRecordDetailActivity : BindActivity<ActivityLoanRecordDetailBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_record_detail

    private val loanRecord
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_RECORD) as? LoanRecord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val record = loanRecord
        if(record == null){
            postOnMainThread {
                finish()
            }
        }else{
            binding.order = record
        }
    }
}
