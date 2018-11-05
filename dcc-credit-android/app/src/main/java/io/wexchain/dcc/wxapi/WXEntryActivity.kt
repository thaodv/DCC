package io.wexchain.dcc.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.dcc.WxApiManager
import org.jetbrains.anko.toast


class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WxApiManager.wxapi.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WxApiManager.wxapi.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        when (resp?.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED, BaseResp.ErrCode.ERR_USER_CANCEL -> finish()
            BaseResp.ErrCode.ERR_OK -> {
                val code = (resp as SendAuth.Resp).code
                GardenOperations.boundWechat(code)
                        .subscribeBy {
                            Log.e("memberId", it)
                            toast(it)
                        }
            }
        }

    }

    override fun onReq(req: BaseReq?) {
        println(req)
    }
}