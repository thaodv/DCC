package io.wexchain.dcc.wxapi

import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.WxApiManager
import io.wexchain.ipfs.utils.doMain


class WXEntryActivity : BaseCompatActivity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.translucent)
        WxApiManager.wxapi.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WxApiManager.wxapi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        if (resp?.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            val launchMiniProResp = resp as WXLaunchMiniProgram.Resp
            val extraData = launchMiniProResp.extMsg // 对应JsApi navigateBackApplication中的extraData字段数据
            //TODO 小程序跳转传参
        }

        when (resp?.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED, BaseResp.ErrCode.ERR_USER_CANCEL -> finish()
            BaseResp.ErrCode.ERR_OK -> {
                val code = (resp as SendAuth.Resp).code
                GardenOperations.boundWechat(code)
                        .flatMap {
                            GardenOperations.loginWithCurrentPassport(this)
                        }
                        .withLoading()
                        .doFinally {
                            finish()
                        }
                        .subscribe()
            }
            else -> finish()
        }
    }

    override fun onReq(req: BaseReq?) {
        println(req)
        finish()
    }
}