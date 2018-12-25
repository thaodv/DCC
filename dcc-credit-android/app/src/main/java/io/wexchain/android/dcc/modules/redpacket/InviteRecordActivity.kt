package io.wexchain.android.dcc.modules.redpacket

import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityInviteRecordBinding
import io.wexchain.dcc.databinding.ItemRedpacketInviteBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.redpacket.InviteRecordBean

class InviteRecordActivity : BindActivity<ActivityInviteRecordBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_invite_record

    private val adapter = SimpleDataBindAdapter<ItemRedpacketInviteBinding, InviteRecordBean>(
            layoutId = R.layout.item_redpacket_invite,
            variableId = BR.inviteRecordBean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
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
    }

    class RewardsListVm : PagedVm<InviteRecordBean>() {
        override fun loadPage(page: Int): Single<PagedList<InviteRecordBean>> {
            return GardenOperations.refreshToken {
                App.get().marketingApi.queryInviteRecord(it, page.toLong(), 20).check()
            }

        }
    }

}
