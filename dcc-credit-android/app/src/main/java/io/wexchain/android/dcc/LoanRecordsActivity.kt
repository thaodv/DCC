package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemLoanRecordBinding
import io.wexchain.dccchainservice.domain.LoanRecord
import java.io.Serializable

class LoanRecordsActivity : BaseCompatActivity(), ItemViewClickListener<LoanRecord> {

    private val adapter = SimpleDataBindAdapter<ItemLoanRecordBinding, LoanRecord>(
        layoutId = R.layout.item_loan_record,
        variableId = BR.order,
        itemViewClickListener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_list)
        initToolbar()
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        loadData()
    }

    private fun loadData() {
        ScfOperations
            .withScfTokenInCurrentPassport {
                //todo paging
                App.get().scfApi.queryOrders(it, 0, 100)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                //todo merge pages
                adapter.setList(list.items)
            })
    }

    override fun onItemClick(item: LoanRecord?, position: Int, viewId: Int) {
        item?.let {
            if (it.applyId != null) {
                navigateTo(LoanRecordDetailActivity::class.java) {
                    putExtra(Extras.EXTRA_LOAN_RECORD, it as Serializable)
                }
            }else{
                //todo
            }
        }
    }
}
