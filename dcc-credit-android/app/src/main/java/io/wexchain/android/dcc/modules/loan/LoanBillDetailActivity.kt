package io.wexchain.android.dcc.modules.loan

import android.os.Bundle
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanBillDetailBinding
import io.wexchain.dccchainservice.domain.LoanReport

class LoanBillDetailActivity : BindActivity<ActivityLoanBillDetailBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_bill_detail

    private val bill
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_REPORT_BILL) as? LoanReport.Bill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val bill = this.bill
        if(bill == null){
            postOnMainThread { finish() }
        }else{
            binding.bill = bill
        }
    }
}
