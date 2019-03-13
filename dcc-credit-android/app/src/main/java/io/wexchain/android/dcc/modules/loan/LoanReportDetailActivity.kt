package io.wexchain.android.dcc.modules.loan

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.view.recycler.DividerDecoration
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanReportDetailBinding
import io.wexchain.dcc.databinding.ItemReportBillBinding
import io.wexchain.dccchainservice.domain.LoanReport
import java.io.Serializable

class LoanReportDetailActivity : BindActivity<ActivityLoanReportDetailBinding>(),
    ItemViewClickListener<LoanReport.Bill> {
    override fun onItemClick(item: LoanReport.Bill?, position: Int, viewId: Int) {
        item?.let {
            navigateTo(LoanBillDetailActivity::class.java){
                putExtra(Extras.EXTRA_LOAN_REPORT_BILL,it as Serializable)
            }
        }
    }

    override val contentLayoutId: Int
        get() = R.layout.activity_loan_report_detail

    private val report
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_REPORT) as? LoanReport

    private val adapter = SimpleDataBindAdapter<ItemReportBillBinding,LoanReport.Bill>(
        layoutId = R.layout.item_report_bill,
        variableId = BR.bill,
        itemViewClickListener = this@LoanReportDetailActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.rvBills.run {
            addItemDecoration(DividerDecoration(context))
            adapter = this@LoanReportDetailActivity.adapter
        }
        val report = this.report
        if(report == null){
            postOnMainThread {
                finish()
            }
        }else{
            binding.report = report
        }


    }
}
