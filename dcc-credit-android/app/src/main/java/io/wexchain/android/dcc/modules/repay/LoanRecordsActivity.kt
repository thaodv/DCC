package io.wexchain.android.dcc.modules.repay

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.home.LoanActivity
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.LoanRecordsVm
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanRecordsBinding
import io.wexchain.dcc.databinding.ItemLoanRecordBinding
import io.wexchain.dccchainservice.domain.LoanRecordSummary

class LoanRecordsActivity : BindActivity<ActivityLoanRecordsBinding>(), ItemViewClickListener<LoanRecordSummary> {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_records

    private val adapter = SimpleDataBindAdapter<ItemLoanRecordBinding, LoanRecordSummary>(
        layoutId = R.layout.item_loan_record,
        variableId = BR.order,
        itemViewClickListener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.rvList.adapter = adapter
        val vm = getViewModel<LoanRecordsVm>()
        binding.srlList.setOnRefreshListener { srl ->
            vm.refresh { srl.finishRefresh() }
        }
        binding.srlList.setOnLoadMoreListener { srl ->
            vm.loadNext { srl.finishLoadMore() }
        }
        binding.srlList.setRefreshHeader(ClassicsHeader(this))
        binding.srlList.setRefreshFooter(ClassicsFooter(this))
        binding.records = vm
    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    override fun onItemClick(item: LoanRecordSummary?, position: Int, viewId: Int) {
        item?.let {
            navigateTo(LoanRecordDetailActivity::class.java) {
                putExtra(Extras.EXTRA_LOAN_RECORD_ID, it.orderId)
            }
        }
    }

    override fun onBackPressed() {
        finishAndGotoLoan()
    }

    override fun handleHomePressed(): Boolean {
        finishAndGotoLoan()
        return true
    }

    private fun finishAndGotoLoan() {
        finish()
       /* ActivityCompat.finishAfterTransition(this)
        navigateTo(LoanActivity::class.java) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }*/
    }
}
