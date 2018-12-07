package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.modules.cashloan.vm.CashLoanRecordsVm
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.LoanRecordsVm
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashloanRecordsBinding
import io.wexchain.dcc.databinding.ItemCashloanRecordBinding
import io.wexchain.dccchainservice.domain.TnLoanOrder

class CashLoanRecordsActivity : BindActivity<ActivityCashloanRecordsBinding>(), ItemViewClickListener<TnLoanOrder> {

    override val contentLayoutId: Int get() = R.layout.activity_cashloan_records

    private val adapter = SimpleDataBindAdapter<ItemCashloanRecordBinding, TnLoanOrder>(
            layoutId = R.layout.item_cashloan_record,
            variableId = BR.cashorder,
            itemViewClickListener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
    }

    override fun onItemClick(item: TnLoanOrder?, position: Int, viewId: Int) {

    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    private fun initView() {
        binding.rvList.adapter = adapter
        val vm = getViewModel<CashLoanRecordsVm>()
        binding.srlList.setOnRefreshListener { srl ->
            vm.refresh { srl.finishRefresh() }
        }
        binding.srlList.setOnLoadMoreListener { srl ->
            vm.loadNext { srl.finishLoadMore() }
        }
        binding.srlList.setRefreshHeader(ClassicsHeader(this))
        binding.srlList.setRefreshFooter(ClassicsFooter(this))
        vm.records
        binding.vm = vm


    }
}
