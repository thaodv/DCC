package io.wexchain.android.dcc

import android.content.ClipData
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.WxApiManager
import io.wexchain.dcc.databinding.ActivityDccAffiliateBinding

class DccAffiliateActivity : BindActivity<ActivityDccAffiliateBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_dcc_affiliate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        toolbarTitle?.setTextColor(Color.parseColor("#ffffff"))
        initClicks()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        val passed = CertOperations.isIdCertPassed()
        binding.certDone = passed
        if (passed) {
            ScfOperations
                .getScfAccountInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { acc ->
                    binding.account = acc
                }
        }
    }

    private fun initClicks() {
        binding.tvAffRecords.setOnClickListener {
            navigateTo(DccAffiliateRecordsActivity::class.java)
        }
        binding.btnShareInviteCode.setOnClickListener {
            showShareInviteCode()
        }
        binding.btnToCert.setOnClickListener {
            PassportOperations.ensureCaValidity(this) {
                navigateTo(SubmitIdActivity::class.java)
            }
        }
        binding.cardAff.setOnClickListener {
            copyCodeToClipboard()
        }
    }

    private fun showShareInviteCode() {
        showShareInviteCodeToWechat()
    }

    private val shareInviteCodeToWechatDialog by lazy {
        BottomSheetDialog(this).apply {
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
        val code = binding.account?.inviteCode
        code ?: return
        if (toCircle && !WxApiManager.isWXCircleSupported) {
            toast("微信版本不支持分享到朋友圈")
            finish()
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

    private fun copyCodeToClipboard() {
        binding.account?.inviteCode?.let {
            getClipboardManager().primaryClip = ClipData.newPlainText("邀请码", it)
            toast("已复制到剪贴板")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_aff, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_aff_terms -> {
                navigateTo(DccAffiliateTermsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
