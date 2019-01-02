package io.wexchain.android.dcc.modules.home

import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDccEcoRewardsListBinding
import io.wexchain.dcc.databinding.ItemEcoRewardBinding
import io.wexchain.dccchainservice.domain.EcoBonus
import io.wexchain.dccchainservice.domain.PagedList

class DccEcoRewardsListActivity : BindActivity<ActivityDccEcoRewardsListBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_dcc_eco_rewards_list

    private val adapter = SimpleDataBindAdapter<ItemEcoRewardBinding, EcoBonus>(
            layoutId = R.layout.item_eco_reward,
            variableId = BR.bonus
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
    }

    private fun initVm() {
        val vm = getViewModel<RewardsListVm>()
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
        loadTotalIncome()
    }

    private fun loadTotalIncome() {
        val scfApi = App.get().scfApi
        ScfOperations
                .withScfTokenInCurrentPassport {
                    scfApi.getTotalEcoBonus(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { total ->
                    binding.accumIncome = total
                }
    }

    class RewardsListVm : PagedVm<EcoBonus>() {
        override fun loadPage(page: Int): Single<PagedList<EcoBonus>> {
            return ScfOperations.withScfTokenInCurrentPassport {
                App.get().scfApi.queryEcoBonus(it, page.toLong(), 20)
            }
        }
    }
}
