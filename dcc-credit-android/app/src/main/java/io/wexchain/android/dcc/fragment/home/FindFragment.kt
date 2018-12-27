package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.garden.activity.GardenActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenListActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenTaskActivity
import io.wexchain.android.dcc.modules.redpacket.GetRedpacketActivity
import io.wexchain.android.dcc.modules.redpacket.RuleActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentFindBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.redpacket.RedPacketActivityBean
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main


/**
 *Created by liuyang on 2018/9/18.
 */
class FindFragment : BindFragment<FragmentFindBinding>() {

    companion object {
        private const val MATE_DATA = "mate_data"

        fun getInstance(data: String? = null): FindFragment {
            val fragment = FindFragment()
            fragment.arguments = Bundle().apply {
                putString(MATE_DATA, data)
            }
            return fragment
        }

        /**
         * 未开始
         */
        const val create = "https://static.bitphare.com/dapp/redPacket/banner2@2x.png"
        /**
         * 开始
         */
        const val started = "https://static.bitphare.com/dapp/redPacket/banner1@2x.png"
        /**
         * 结束
         */
        const val end = "https://static.bitphare.com/dapp/redPacket/banner3@2x.png"

    }

    private val passport by lazy {
        App.get().passportRepository
    }

    private val data by lazy {
        arguments?.getString(MATE_DATA)
    }

    private var dialog: BaseDialog? = null

    override val contentLayoutId: Int get() = R.layout.fragment_find

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        login()
    }


    fun login() {
        GardenOperations.loginWithCurrentPassport()
                .io_main()
                .withLoading()
                .doOnError {
                    if (it is DccChainServiceException) {
                        toast(it.message!!)
                    }
                }
                .subscribeBy {
                    initVm()
                    if (!data.isNullOrEmpty()) {
                        val list = data!!.split('/')
                        val code = list[0]
                        val playid = list[1]
                        val unionId = list[2]
                        val info = it.first
                        info.player?.let {
                            val localplayid = it.id.toString()
                            if (localplayid != playid) {//不是同一个用户
                                val message = "当前登录的微信账号($localplayid)与BitExpress绑定的微信账号($playid)不一致"
                                BaseDialog(activity!!).TipsDialog(tipmessage = message)
                            }
                            // 跳转到红包领取页面
                            else {
                                if ("redPacket" == code) {
                                    navigateTo(GetRedpacketActivity::class.java)
                                } else {

                                }
                            }
                        }
                    }
                }
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })
        binding.vm = getViewModel()

        if (!GardenOperations.isBound()) {
            showBoundDialog()
        } else {
            binding.vm?.refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        if (GardenOperations.isBound()) {
            binding.vm?.refresh()
        }

        getRedPacketActivity()

    }

    private fun getRedPacketActivity() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getRedPacketActivity(it).check()
                }
                .doMain()
                .subscribe({

                    /*if (null != it.coverImgUrl) {
                        binding.imgUrl = it.coverImgUrl
                    }*/

                    if (it.status == RedPacketActivityBean.Status.STARTED || it.status == RedPacketActivityBean.Status.ENDED) {
                        binding.llRedpacket.checkBoundClick {
                            navigateTo(GetRedpacketActivity::class.java)
                        }
                    } else {
                        binding.llRedpacket.checkBoundClick {
                            navigateTo(RuleActivity::class.java)
                        }
                    }

                    when {
                        it.status == RedPacketActivityBean.Status.STARTED -> binding.imgUrl = started
                        it.status == RedPacketActivityBean.Status.ENDED -> binding.imgUrl = end
                        else -> binding.imgUrl = create
                    }

                }, {
                })
    }

    private fun initClick() {
        binding.gardenTask.checkBoundClick {
            navigateTo(GardenTaskActivity::class.java)
        }
        binding.findInGarden.checkBoundClick {
            navigateTo(GardenActivity::class.java)
        }
        binding.findShare.checkBoundClick {
            GardenOperations.shareWechat {
                toast(it)
            }
        }
        binding.findGardenCard.checkBoundClick {
            navigateTo(GardenActivity::class.java)
        }
        binding.findGardenList.checkBoundClick {
            navigateTo(GardenListActivity::class.java)
        }
        binding.findGardenList2.checkBoundClick {
            navigateTo(GardenListActivity::class.java)
        }
        binding.findZhishiCard.checkBoundClick {
            GardenOperations.startWechat {
                toast(it)
            }
        }

    }

    private fun View.checkBoundClick(event: () -> Unit) {
        this.onClick {
            if (GardenOperations.isLogin()) {
                if (GardenOperations.isBound()) {
                    event()
                } else {
                    showBoundDialog()
                }
            } else {
                toast("用户未登陆")
                login()
            }
        }
    }

    private fun showBoundDialog() {
        if (dialog == null) {
            dialog = BaseDialog(activity!!)
        } else {
            dialog!!.show()
            return
        }
        if (dialog!!.isShowing) {
            return
        }
        dialog!!.BoundWechatDialog()
                .onClick(onConfirm = {
                    GardenOperations.wechatLogin {
                        toast(it)
                    }
                })
    }

}
