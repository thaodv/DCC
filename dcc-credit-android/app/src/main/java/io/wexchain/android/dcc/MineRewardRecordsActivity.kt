package io.wexchain.android.dcc

import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMineRewardRecordsBinding
import io.wexchain.dcc.databinding.ItemMineRewardRecordBinding
import io.wexchain.dccchainservice.domain.EcoBonus
import io.wexchain.dccchainservice.domain.PagedList

class MineRewardRecordsActivity : BindActivity<ActivityMineRewardRecordsBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_mine_reward_records

    private val adapter = SimpleDataBindAdapter<ItemMineRewardRecordBinding, EcoBonus>(
        layoutId = R.layout.item_mine_reward_record,
        variableId = BR.bonus
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initData()
        loadRewards()
    }

    private fun loadRewards() {
        binding.rvList.adapter = adapter
        binding.srlList.autoRefresh()
        ScfOperations
            .withScfTokenInCurrentPassport {
                App.get().scfApi.queryMineRewardTotalAmount(it)
            }
            .subscribe { amount ->
                binding.totalAmount = amount
            }
    }

    private fun initData() {
        val vm = getViewModel<MineRewardsListVm>()
        val srl = binding.srlList
        srl.setOnRefreshListener { sr ->
            vm.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm.loadNext { sr.finishLoadMore() }
        }
        binding.vm = vm
    }

    class MineRewardsListVm : PagedVm<EcoBonus>() {
        override fun loadPage(page: Int): Single<PagedList<EcoBonus>> {
            return ScfOperations.withScfTokenInCurrentPassport {
                App.get().scfApi.queryMineRewardRecords(it, page.toLong())
            }
        }
    }
}
