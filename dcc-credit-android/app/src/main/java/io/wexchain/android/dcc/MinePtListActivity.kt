package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.Single
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMinePtListBinding
import io.wexchain.dcc.databinding.ItemMinePtRecordBinding
import io.wexchain.dccchainservice.domain.MineContributionRecord
import io.wexchain.dccchainservice.domain.PagedList

class MinePtListActivity : BindActivity<ActivityMinePtListBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_mine_pt_list

    private val adapter = SimpleDataBindAdapter<ItemMinePtRecordBinding, MineContributionRecord>(
            layoutId = R.layout.item_mine_pt_record,
            variableId = BR.record
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initData()
        initLoad()
    }

    private fun initLoad() {
        if (adapter.itemCount == 0) {
            findViewById<SmartRefreshLayout>(R.id.srl_list).autoRefresh()
        }
    }

    private fun initData() {
        val vm = getViewModel<MinePtListVm>()
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        val srl = findViewById<SmartRefreshLayout>(R.id.srl_list)
        srl.setOnRefreshListener { sr ->
            vm.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm.loadNext { sr.finishLoadMore() }
        }
        binding.vm = vm
    }

    class MinePtListVm : PagedVm<MineContributionRecord>() {
        override fun loadPage(page: Int): Single<PagedList<MineContributionRecord>> {
            return ScfOperations.withScfTokenInCurrentPassport {
                App.get().scfApi.queryMineContributionRecord(it, page.toLong())
            }
        }

    }
}
