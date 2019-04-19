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
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.cert.MyCreditActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.view.dialog.GetRedpacketDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGetRedpacketBinding
import io.wexchain.dcc.databinding.ItemRedpacketInfoBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.redpacket.QueryStoreBean
import io.wexchain.dccchainservice.domain.redpacket.RedPacketActivityBean
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.ipfs.utils.doMain

class GetRedpacketActivity : BindActivity<ActivityGetRedpacketBinding>(), ItemViewClickListener<QueryStoreBean> {

    override val contentLayoutId: Int get() = R.layout.activity_get_redpacket

    private val adapter = Adapter(this)

    private lateinit var mRvRedpacketGet: RecyclerView

    private lateinit var mActivityStatus: RedPacketActivityBean.Status

    private var mStartTime: Long = 0
    private var mEndTime: Long = 0

    private var hasGet: Boolean = false

    private lateinit var getMoney: String

    var redPacketId: String? = ""
    /**
     * 为null时候代表没有解锁
     */
    var hasUnLock: String? = ""

    @SuppressLint("HandlerLeak")
    private val sHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    mRvRedpacketGet.smoothScrollBy(0, 60)
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

        //getRedpacketDialog = GetRedpacketDialog(this)

        binding.rvList.isNestedScrollingEnabled = false
        binding.rvList.adapter = adapter

        binding.ivInvite.setOnClickListener {
            navigateTo(InviteRecordActivity::class.java) {
                putExtra(Extras.EXTRA_REDPACKET_START_TIME, mStartTime)
                putExtra(Extras.EXTRA_REDPACKET_END_TIME, mEndTime)
            }
        }

        binding.tvRule.setOnClickListener {
            navigateTo(RuleActivity::class.java) {
                putExtra(Extras.EXTRA_REDPACKET_START_TIME, mStartTime)
                putExtra(Extras.EXTRA_REDPACKET_END_TIME, mEndTime)
            }
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
            navigateTo(MyCreditActivity::class.java)
        }

        binding.btRealname.setOnClickListener {
            navigateTo(MyCreditActivity::class.java)
        }

        binding.rlGetRedpacket.setOnClickListener {
            // 活动结束
            if (mActivityStatus == RedPacketActivityBean.Status.ENDED) {
                val getRedpacketDialog = GetRedpacketDialog(this)
                getRedpacketDialog.setTitle(getString(R.string.tips))
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
                getRedpacketDialog.show()
            } else {
                // 未解锁
                if (null == hasUnLock) {
                    val getRedpacketDialog = GetRedpacketDialog(this)
                    getRedpacketDialog.setTitle(getString(R.string.tips))
                    getRedpacketDialog.setIbtCloseVisble(View.GONE)
                    getRedpacketDialog.setText("您还没有解锁的红包！\n邀请好友为您助力~")
                    getRedpacketDialog.setBtnText("继续邀请", getString(R.string.cancel))
                    getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                        override fun cancel() {
                            GardenOperations.shareWechatRedPacket {
                                toast(it)
                            }
                        }

                        override fun sure() {

                        }
                    })
                    getRedpacketDialog.show()
                }
                // 已解锁
                else {
                    // 未领取
                    if (!hasGet) {
                        // 无红包可领取
                        if (null == redPacketId) {
                            val getRedpacketDialog = GetRedpacketDialog(this)
                            getRedpacketDialog.setTitle(getString(R.string.tips))
                            getRedpacketDialog.setIbtCloseVisble(View.GONE)
                            getRedpacketDialog.setText("每位用户限领1个红包！\n可领红包已领光！")
                            getRedpacketDialog.setBtnText("继续邀请", getString(R.string.cancel))
                            getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                                override fun cancel() {
                                    GardenOperations.shareWechatRedPacket {
                                        toast(it)
                                    }
                                }

                                override fun sure() {

                                }
                            })
                            getRedpacketDialog.show()
                        }
                        // 可领取
                        else {
                            val getRedpacketDialog = GetRedpacketDialog(this)
                            getRedpacketDialog.setTitle(getString(R.string.tips))
                            getRedpacketDialog.setIbtCloseVisble(View.VISIBLE)
                            getRedpacketDialog.setText("每位用户限领1个红包！\n确认领取【$getMoney】元红包？")
                            getRedpacketDialog.setBtnText("继续邀请", "领取红包")
                            getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                                override fun cancel() {
                                    GardenOperations.shareWechatRedPacket {
                                        toast(it)
                                    }
                                }

                                override fun sure() {
                                    GardenOperations
                                            .refreshToken {
                                                App.get().marketingApi.pickRedPacket(it, redPacketId!!.toLong()).check()
                                            }
                                            .doMain()
                                            .withLoading()
                                            .subscribe({
                                                val getRedpacketDialog = GetRedpacketDialog(this@GetRedpacketActivity)
                                                getRedpacketDialog.setTitle("已领取")
                                                getRedpacketDialog.setIbtCloseVisble(View.GONE)
                                                getRedpacketDialog.setText("微信红包稍后将发放到您的微信账户，请至【微信钱包】的【零钱明细】中查看！")
                                                getRedpacketDialog.setBtnText("我知道了", "")
                                                getRedpacketDialog.setBtSureVisble(View.GONE)
                                                getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                                                    override fun cancel() {
                                                        initDataFromNet()
                                                    }

                                                    override fun sure() {

                                                    }

                                                })
                                                getRedpacketDialog.show()
                                            }, {
                                                val err = it as DccChainServiceException
                                                if (err.businessCode == "BONUS_IS_EMPTY") {
                                                    val getRedpacketDialog = GetRedpacketDialog(this@GetRedpacketActivity)
                                                    getRedpacketDialog.setTitle(getString(R.string.tips))
                                                    getRedpacketDialog.setIbtCloseVisble(View.GONE)
                                                    getRedpacketDialog.setText("可领红包已领完")
                                                    getRedpacketDialog.setBtnText("继续邀请", getString(R.string.cancel))
                                                    getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                                                        override fun cancel() {
                                                            GardenOperations.shareWechatRedPacket {
                                                                toast(it)
                                                            }
                                                        }

                                                        override fun sure() {
                                                        }

                                                    })
                                                    getRedpacketDialog.show()
                                                } else if (err.businessCode == "REDPACKET_OPEN_ID_NOT_FOUND") {
                                                    val getRedpacketDialog = GetRedpacketDialog(this@GetRedpacketActivity)
                                                    getRedpacketDialog.setTitle(getString(R.string.tips))
                                                    getRedpacketDialog.setIbtCloseVisble(View.GONE)
                                                    getRedpacketDialog.setText("需要登录BitExpress小程序，才可以领取红包！")
                                                    getRedpacketDialog.setBtnText("去小程序", getString(R.string.cancel))
                                                    getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                                                        override fun cancel() {
                                                            GardenOperations.startWechatRedPacket {
                                                                toast(it)
                                                            }
                                                        }

                                                        override fun sure() {
                                                        }

                                                    })
                                                    getRedpacketDialog.show()
                                                } else {
                                                    toast(it.message ?: getString(R.string.system_error))
                                                }

                                            })
                                }
                            })
                            getRedpacketDialog.show()
                        }
                    } else {
                        val getRedpacketDialog = GetRedpacketDialog(this)
                        getRedpacketDialog.setTitle("已领取")
                        getRedpacketDialog.setIbtCloseVisble(View.GONE)
                        getRedpacketDialog.setText("微信红包稍后将发放到您的微信账户，请至【微信钱包】的【零钱明细】中查看！")
                        getRedpacketDialog.setBtnText("我知道了", "")
                        getRedpacketDialog.setBtSureVisble(View.GONE)
                        getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                            override fun cancel() {

                            }

                            override fun sure() {

                            }
                        })
                        getRedpacketDialog.show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        initDataFromNet()
    }

    @SuppressLint("SetTextI18n")
    fun initDataFromNet() {
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

                            redPacketId = it.second.redPacketStocks.actualUnlockStock

                            hasUnLock = it.second.redPacketStocks.expectedUnlockStock

                            binding.records = it.second.redPacketStocks.redPacketStocks

                            binding.tvNum.text = it.second.inviteInfo.inviteCount

                            hasGet = it.second.inviteInfo.status

                            if (hasGet) {
                                binding.rlGetRedpacket.background = resources.getDrawable(R.drawable.img_redpacket_got)
                                getMoney = it.second.inviteInfo.redPacketAmount
                                binding.tvTip.text = "已领取 ￥ "
                            } else {
                                binding.rlGetRedpacket.background = resources.getDrawable(R.drawable.img_redpacket_get)
                                getMoney = if (null == it.second.redPacketStocks.amount) "0" else it.second.redPacketStocks.amount
                                binding.tvTip.text = "已解锁 ￥ "
                            }

                            binding.tvMoney.text = getMoney

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
                                binding.llEmpty.visibility = View.GONE
                                mRvRedpacketGet.visibility = View.VISIBLE
                                val testAdapter = RedPacketGetAdapter(res)

                                mRvRedpacketGet.adapter = testAdapter

                                if (res.size > 6) {
                                    val msg = Message()
                                    msg.what = 1
                                    sHandler.sendMessageDelayed(msg, 3000)
                                }
                            } else {
                                binding.llEmpty.visibility = View.VISIBLE
                                mRvRedpacketGet.visibility = View.GONE
                            }
                        },
                        onError = {
                            toast(it.message ?: getString(R.string.system_error))
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

    }

}
