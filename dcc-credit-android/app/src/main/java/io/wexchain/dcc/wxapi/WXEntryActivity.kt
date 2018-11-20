package io.wexchain.dcc.wxapi

import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.noStatusBar
import io.wexchain.android.common.noTitleBar
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.LoadingActivity
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.dcc.WxApiManager
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result


class WXEntryActivity : BaseCompatActivity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        noStatusBar()
        noTitleBar()
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
                if (resp.type == 1) {
                    val code = (resp as SendAuth.Resp).code
                    GardenOperations.boundWechat(code)
                            .flatMap {
                                GardenOperations.loginWithCurrentPassport()
                            }
                            .withLoading()
                            .doOnError {
                                if (it is DccChainServiceException) {
                                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.WECHAT_HAD_BEEN_BOUND) {
                                        toast(it.message!!)
                                    }
                                }
                            }
                            .doFinally {
                                finish()
                            }
                            .subscribe()
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
        /*val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtras(getIntent())
        startActivity(intent)*/
        finish()
    }

    private fun goToShowMsg(showReq: ShowMessageFromWX.Req) {
        val wxMsg = showReq.message
        val obj = wxMsg.mediaObject as WXAppExtendObject

        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra("data", obj.extInfo)
        startActivity(intent)
        finish()
    }
}