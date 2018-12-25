package io.wexchain.android.dcc.modules.redpacket

import android.os.Bundle
import android.view.View
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.MyCreditNewActivity
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGetRedpacketBinding
import io.wexchain.dcc.databinding.ItemRedpacketInfoBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.redpacket.QueryStoreBean
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain

class GetRedpacketActivity : BindActivity<ActivityGetRedpacketBinding>(), ItemViewClickListener<QueryStoreBean> {


    override val contentLayoutId: Int get() = R.layout.activity_get_redpacket

    private val adapter = Adapter(this)

    private lateinit var redPacketOrderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.rvList.isNestedScrollingEnabled = false
        binding.rvList.adapter = adapter

        Singles.zip(
                App.get().chainGateway.getCertData(App.get().passportRepository.getCurrentPassport()!!.address, ChainGateway.BUSINESS_ID).check(),
                GardenOperations.refreshToken {
                    App.get().marketingApi.getRedPacket(it).check()
                })
                .doMain()
                .withLoading()
                .subscribeBy(
                        onSuccess = {
                            if ("" == it.first.content.digest1) {
                                binding.rlRealname.visibility = View.VISIBLE
                                binding.rlGetRedpacket.visibility = View.GONE

                                binding.rlRealname.setOnClickListener {
                                    navigateTo(MyCreditNewActivity::class.java)
                                }

                            } else {
                                binding.rlRealname.visibility = View.GONE
                                binding.rlGetRedpacket.visibility = View.VISIBLE

                                binding.rlGetRedpacket.setOnClickListener {

                                }
                            }

                            binding.records = it.second.redPacketStocks

                            binding.tvNum.text = it.second.inviteInfo.inviteCount

                            redPacketOrderId = it.second.inviteInfo.redPacketOrderId

                            binding.tvRealTime.text = "活动时间 " + DateUtil.getStringTime(it.second.activity.from, "yyyy.MM.dd") + " ~ " + DateUtil.getStringTime(it.second.activity.to, "yyyy.MM.dd")
                            binding.tvGetTime.text = "活动时间 " + DateUtil.getStringTime(it.second.activity.from, "yyyy.MM.dd") + " ~ " + DateUtil.getStringTime(it.second.activity.to, "yyyy.MM.dd")

                        },
                        onError = {

                        }
                )

        binding.llInvite.setOnClickListener {
            navigateTo(InviteRecordActivity::class.java)
        }

        binding.tvRule.setOnClickListener {
            navigateTo(RuleActivity::class.java)
        }

        binding.llSavePoster.setOnClickListener {
            navigateTo(PosterActivity::class.java)
        }

    }

    private class Adapter(itemViewClickListener: ItemViewClickListener<QueryStoreBean>) :
            DataBindAdapter<ItemRedpacketInfoBinding, QueryStoreBean>(
                    layout = R.layout.item_redpacket_info,
                    itemDiffCallback = defaultItemDiffCallback(),
                    itemViewClickListener = itemViewClickListener
            ) {
        override fun bindData(binding: ItemRedpacketInfoBinding, item: QueryStoreBean?) {
            binding.storebean = item
        }

    }

    override fun onItemClick(item: QueryStoreBean?, position: Int, viewId: Int) {

    }

}
