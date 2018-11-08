package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.View
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.garden.activity.GardenListActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenTaskActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenActivity
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.WxApiManager
import io.wexchain.dcc.databinding.FragmentFindBinding


/**
 *Created by liuyang on 2018/9/18.
 */
class FindFragment : BindFragment<FragmentFindBinding>() {

    private val passport by lazy {
        App.get().passportRepository
    }

    private var dialog: BaseDialog? = null

    override val contentLayoutId: Int
        get() = R.layout.fragment_find

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initVm()
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })
        App.get().isLogin.observe(this, Observer {
            it?.let {
                if (it) {
                    if (!GardenOperations.isBound()) {
                        showBoundDialog()
                    } else {
                        binding.vm = getViewModel()
                    }
                }
            }
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
            showShareInviteCodeToWechat()
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
            GardenOperations.startWechat{
                toast(it)
            }
        }
    }

    fun View.checkBoundClick(event: () -> Unit) {
        this.onClick {
            if (GardenOperations.isBound()) {
                event()
            } else {
                showBoundDialog()
            }
        }
    }

    private val shareInviteCodeToWechatDialog by lazy {
        BottomSheetDialog(activity!!).apply {
            setContentView(R.layout.dialog_share_invite_code)
            findViewById<View>(R.id.fl_share_wechat_circle)!!.setOnClickListener {
                sendWechatShare(true)
                dismiss()
            }
            findViewById<View>(R.id.fl_share_wechat_friend)!!.setOnClickListener {
                sendWechatShare(false)
                dismiss()
            }
        }
    }

    private fun sendWechatShare(toCircle: Boolean) {
        val code = ""
        if (toCircle && !WxApiManager.isWXCircleSupported) {
            toast("微信版本不支持分享到朋友圈")
            activity?.finish()
        }
        val page = WXWebpageObject("http://open.dcc.finance/dapp/invite/index.html?code=$code")
        val wxMediaMessage = WXMediaMessage(page).apply {
            title = "我在BitExpress，输入邀请码“$code”获得奖励，快来加入吧"
            description = "我在BitExpress，输入邀请码“$code”获得奖励，快来加入吧"
            setThumbImage(BitmapFactory.decodeResource(resources, R.drawable.logo_share))
        }
        val req = SendMessageToWX.Req().apply {
            transaction = buildTransaction(code, toCircle)
            message = wxMediaMessage
            scene = if (toCircle) SendMessageToWX.Req.WXSceneTimeline else SendMessageToWX.Req.WXSceneSession
        }
        WxApiManager.wxapi.sendReq(req)
    }

    private fun buildTransaction(code: String, toCircle: Boolean): String {
        return "share_${code}_${System.currentTimeMillis()}_${if (toCircle) 1 else 0}"
    }

    private fun showShareInviteCodeToWechat() {
        shareInviteCodeToWechatDialog.show()
    }

    fun showBoundDialog() {
        if (dialog == null){
            dialog = BaseDialog(activity!!)
        }
        if (dialog!!.isShowing){
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