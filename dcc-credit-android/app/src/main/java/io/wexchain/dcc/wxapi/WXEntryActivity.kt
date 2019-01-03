package io.wexchain.dcc.wxapi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import io.wexchain.android.common.*
import io.wexchain.android.common.base.ActivityCollector
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.modules.other.LoadingActivity
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.garden.activity.GardenActivity
import io.wexchain.android.dcc.modules.garden.activity.GardenTaskActivity
import io.wexchain.android.dcc.view.dialog.BaseDialog
import io.wexchain.android.dcc.view.dialog.ShowRedPacketDialog
import io.wexchain.dcc.WxApiManager
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain


class WXEntryActivity : BaseCompatActivity(), IWXAPIEventHandler {

    private lateinit var dialog: BaseDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        fullWindow()
        super.onCreate(savedInstanceState)
        WxApiManager.wxapi.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WxApiManager.wxapi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        if (resp == null) {
            finish()
            return
        }
        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                if (resp.type == ConstantsAPI.COMMAND_SENDAUTH) {
                    val code = (resp as SendAuth.Resp).code
                    GardenOperations.boundWechat(code)
                            .flatMap {
                                GardenOperations.loginWithCurrentPassport()
                            }
                            .doMain()
                            .withLoading()
                            .subscribe({
                                val sp = getSharedPreferences("setting", Context.MODE_PRIVATE)
                                if (sp.getBoolean("sunshine_tip_first_into", true)) {

                                    val showRedPacketDialog = ShowRedPacketDialog(this)
                                    showRedPacketDialog.setOnClickListener(object : ShowRedPacketDialog.OnClickListener {
                                        override fun ok() {
                                            sp.edit().putBoolean("sunshine_tip_first_into", false).commit()
                                            finish()
                                        }

                                        override fun goToTask() {
                                            navigateTo(GardenTaskActivity::class.java)
                                            sp.edit().putBoolean("sunshine_tip_first_into", false).commit()
                                            finish()
                                        }
                                    })
                                    showRedPacketDialog.show()
                                }else{
                                    finish()
                                }
                            }, {
                                if (it is DccChainServiceException) {
                                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.WECHAT_HAD_BEEN_BOUND) {
                                        toast(it.message!!)
                                    }
                                }
                            })
                } else if (resp.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
                    val launchMiniProResp = resp as WXLaunchMiniProgram.Resp
                    val extraData = launchMiniProResp.extMsg
                    toBeApp(extraData)
                } else {
                    finish()
                }
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> finish()
            BaseResp.ErrCode.ERR_AUTH_DENIED -> finish()
            BaseResp.ErrCode.ERR_UNSUPPORT -> finish()
            else -> finish()
        }
    }

    override fun onReq(req: BaseReq?) {
        when (req?.type) {
            ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX -> goToGetMsg()
            ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX -> goToShowMsg(req as ShowMessageFromWX.Req)
            else -> {
                finish()
            }
        }
    }

    private fun goToGetMsg() {
        /*  val intent = Intent(this, LoadingActivity::class.java)
          intent.putExtras(getIntent())
          startActivity(intent)*/
        finish()
    }

    private fun goToShowMsg(showReq: ShowMessageFromWX.Req) {
        val wxMsg = showReq.message
        val obj = wxMsg.mediaObject as WXAppExtendObject

        toBeApp(obj.extInfo)
    }

    private fun toBeApp(data: String) {
        if (ActivityCollector.isExistActivity("io.wexchain.android.dcc.modules.home.HomeActivity")) {
            navigateTo(GardenActivity::class.java)
            finish()
        } else {
            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
            finishAllActivity()
        }
    }

}
