package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustRechargeRecordsBinding
import io.wexchain.dcc.databinding.ItemTrustRechargeRecordBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.trustpocket.QueryDepositOrderPageBean

class TrustRechargeRecordsActivity : BindActivity<ActivityTrustRechargeRecordsBinding>(), ItemViewClickListener<QueryDepositOrderPageBean> {


    override val contentLayoutId: Int get() = R.layout.activity_trust_recharge_records

    private val adapter = SimpleDataBindAdapter<ItemTrustRechargeRecordBinding, QueryDepositOrderPageBean>(
            layoutId = R.layout.item_trust_recharge_record,
            variableId = BR.bean,
            itemViewClickListener = this@TrustRechargeRecordsActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mCode = intent.getStringExtra("code")

        val vm = getViewModel<RechargeRecordVm>()
        val srl = binding.srlList

        srl.setOnRefreshListener { sr ->
            vm.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm.loadNext { sr.finishLoadMore() }
        }
        binding.rvList.adapter = adapter
        binding.vm = vm
    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    override fun onItemClick(item: QueryDepositOrderPageBean?, position: Int, viewId: Int) {
        navigateTo(TrustRechargeDetailActivity::class.java) {
            putExtra("id", item!!.id)
        }
    }

    class RechargeRecordVm : PagedVm<QueryDepositOrderPageBean>() {
        override fun loadPage(page: Int): Single<PagedList<QueryDepositOrderPageBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.queryDepositOrderPage(it, mCode, page, 20).check()
            }

        }
    }

    companion object {
        var mCode: String = ""
    }
}
