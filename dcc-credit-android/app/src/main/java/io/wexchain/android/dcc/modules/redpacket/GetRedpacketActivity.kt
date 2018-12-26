package io.wexchain.android.dcc.modules.redpacket

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.MyCreditNewActivity
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.view.dialog.GetRedpacketDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGetRedpacketBinding
import io.wexchain.dcc.databinding.ItemRedpacketInfoBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.redpacket.QueryStoreBean
import io.wexchain.dccchainservice.domain.redpacket.RedPacketActivityBean
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain

class GetRedpacketActivity : BindActivity<ActivityGetRedpacketBinding>(), ItemViewClickListener<QueryStoreBean> {


    override val contentLayoutId: Int get() = R.layout.activity_get_redpacket

    private val adapter = Adapter(this)

    private lateinit var redPacketOrderId: String

    private var mRvRedpacketGet: RecyclerView? = null

    private lateinit var mActivityStatus: RedPacketActivityBean.Status

    var mStartTime: Long = 0
    var mEndTime: Long = 0

    @SuppressLint("HandlerLeak")
    private val sHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    mRvRedpacketGet!!.smoothScrollBy(0, 60)
                    val message = Message()
                    message.what = 1
                    this.removeMessages(1)
                    this.sendMessageDelayed(message, 3000)
                }
                else -> {
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        mRvRedpacketGet = findViewById(R.id.rv_redpacket_get)

        binding.rvList.isNestedScrollingEnabled = false
        binding.rvList.adapter = adapter



        binding.llInvite.setOnClickListener {
            navigateTo(InviteRecordActivity::class.java) {
                putExtra(Extras.EXTRA_REDPACKET_START_TIME, mStartTime)
                putExtra(Extras.EXTRA_REDPACKET_END_TIME, mEndTime)
            }
        }

        binding.tvRule.setOnClickListener {
            navigateTo(RuleActivity::class.java)
        }

        binding.llSavePoster.setOnClickListener {
            navigateTo(PosterActivity::class.java) {
                putExtra(Extras.EXTRA_REDPACKET_START_TIME, mStartTime)
                putExtra(Extras.EXTRA_REDPACKET_END_TIME, mEndTime)
            }
        }

        binding.btInvite.setOnClickListener {
            GardenOperations.shareWechatRedPacket {
                toast(it)
            }
        }

        binding.rlRealname.setOnClickListener {
            navigateTo(MyCreditNewActivity::class.java)
        }

        binding.btRealname.setOnClickListener {
            navigateTo(MyCreditNewActivity::class.java)
        }

        binding.rlGetRedpacket.setOnClickListener {

        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        Singles.zip(
                App.get().chainGateway.getCertData(App.get().passportRepository.getCurrentPassport()!!.address, ChainGateway.BUSINESS_ID).check(),
                GardenOperations.refreshToken {
                    App.get().marketingApi.getRedPacket(it).check()
                },
                GardenOperations.refreshToken {
                    App.get().marketingApi.getRedPacketRecord(it).check()
                })
                .doMain()
                .withLoading()
                .subscribeBy(
                        onSuccess = {
                            if ("" == it.first.content.digest1) {
                                binding.rlRealname.visibility = View.VISIBLE
                                binding.rlGetRedpacket.visibility = View.GONE

                            } else {

                                binding.rlRealname.visibility = View.GONE
                                binding.rlGetRedpacket.visibility = View.VISIBLE
                            }

                            binding.records = it.second.redPacketStocks

                            binding.tvNum.text = it.second.inviteInfo.inviteCount

                            redPacketOrderId = it.second.inviteInfo.redPacketOrderId

                            mStartTime = it.second.activity.from
                            mEndTime = it.second.activity.to
                            mActivityStatus = it.second.activity.status

                            var endStr = ""

                            if (mActivityStatus == RedPacketActivityBean.Status.ENDED) {
                                endStr = "（已结束）"
                            }


                            binding.tvRealTime.text = "活动时间 " + DateUtil.getStringTime(mStartTime, "yyyy.MM.dd") + " ~ " + DateUtil.getStringTime(mEndTime, "yyyy.MM.dd") + endStr
                            binding.tvGetTime.text = "活动时间 " + DateUtil.getStringTime(mStartTime, "yyyy.MM.dd") + " ~ " + DateUtil.getStringTime(mEndTime, "yyyy.MM.dd") + endStr

                            val res = it.third.items

                            if (null != res && res.isNotEmpty()) {
                                val testAdapter = RedPacketGetAdapter(res)

                                mRvRedpacketGet!!.adapter = testAdapter

                                if (res.size > 6) {
                                    val msg = Message()
                                    msg.what = 1
                                    sHandler.sendMessageDelayed(msg, 3000)
                                }
                            }

                        },
                        onError = {

                        }
                )

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


        val getRedpacketDialog = GetRedpacketDialog(this)

        // 活动结束
        if (mActivityStatus == RedPacketActivityBean.Status.ENDED) {
            getRedpacketDialog.setTitle("提示")
            getRedpacketDialog.setIbtCloseVisble(View.GONE)
            getRedpacketDialog.setText("本次活动已结束。非常感谢您的参与和关注！")
            getRedpacketDialog.setBtnText("我知道了", "")
            getRedpacketDialog.setBtSureVisble(View.GONE)
            getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                override fun cancel() {

                }

                override fun sure() {

                }

            })
        } else {
            // 未解锁
            if ("0" != item!!.needInviteCount) {
                getRedpacketDialog.setTitle("提示")
                getRedpacketDialog.setIbtCloseVisble(View.GONE)
                getRedpacketDialog.setText("您还没有解锁的红包！\n邀请好友为您助力~")
                getRedpacketDialog.setBtnText("继续邀请", "取消")
                getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                    override fun cancel() {
                        GardenOperations.shareWechatRedPacket {
                            toast(it)
                        }
                    }
                    override fun sure() {

                    }
                })
            }
            // 已领完
            else if ("0" == item!!.stockCount) {
                getRedpacketDialog.setTitle("提示")
                getRedpacketDialog.setIbtCloseVisble(View.GONE)
                getRedpacketDialog.setText("每位用户限领1个红包！\n可领红包已领光！")
                getRedpacketDialog.setBtnText("继续邀请", "取消")
                getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                    override fun cancel() {
                        GardenOperations.shareWechatRedPacket {
                            toast(it)
                        }
                    }

                    override fun sure() {
                    }

                })
            }
            // 可领取
            else {
                getRedpacketDialog.setTitle("提示")
                getRedpacketDialog.setIbtCloseVisble(View.VISIBLE)
                getRedpacketDialog.setText("每位用户限领1个红包！\n确认领取【" + item.amount + "】元红包？")
                getRedpacketDialog.setBtnText("继续邀请", "领取红包")
                getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                    override fun cancel() {
                        GardenOperations.shareWechatRedPacket {
                            toast(it)
                        }
                    }

                    override fun sure() {
                        toast("get_redpacket")
                    }

                })
            }
        }

        getRedpacketDialog.show()

    }

}
