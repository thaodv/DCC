package io.wexchain.android.dcc.modules.redpacket

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityInviteRecordBinding
import io.wexchain.dcc.databinding.ItemRedpacketInviteBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.redpacket.InviteRecordBean
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain

class InviteRecordActivity : BindActivity<ActivityInviteRecordBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_invite_record

    private val mStartTime get() = intent.getLongExtra(Extras.EXTRA_REDPACKET_START_TIME, 0)
    private val mEndTime get() = intent.getLongExtra(Extras.EXTRA_REDPACKET_END_TIME, 0)

    private val adapter = SimpleDataBindAdapter<ItemRedpacketInviteBinding, InviteRecordBean>(
            layoutId = R.layout.item_redpacket_invite,
            variableId = BR.inviteRecordBean
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)


        GardenOperations
                .refreshToken {
                    App.get().marketingApi.queryInviteInfo(it).check()
                }
                .doMain()
                .subscribe({
                    binding.tvNum.text = it.inviteCount
                }, {

                })

        binding.tvTime.text = DateUtil.getStringTime(mStartTime, "yyyy.MM.dd") + " ~ " + DateUtil.getStringTime(mEndTime, "yyyy.MM.dd")


        val vm = getViewModel<RewardsListVm>()
        val srl = binding.srlList

        vm.checkData.observe(this, Observer {
            val status = binding.llEmpty.visibility
            if (status != it!!) {
                binding.llEmpty.visibility = it
            }
        })

        srl.setOnRefreshListener { sr ->
            vm.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm.loadNext { sr.finishLoadMore() }
        }
        binding.rvList.adapter = adapter
        binding.vm = vm

        binding.btInvite.setOnClickListener {
            GardenOperations.shareWechatRedPacket {
                toast(it)
            }
        }

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
