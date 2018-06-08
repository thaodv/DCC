package io.wexchain.dcc.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import io.wexchain.dcc.WxApiManager

class WXEntryActivity:Activity(),IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WxApiManager.wxapi.handleIntent(intent,this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WxApiManager.wxapi.handleIntent(intent,this)
    }

    override fun onResp(resp: BaseResp?) {
        println("${resp?.errCode} : ${resp?.errStr} from ${resp?.transaction}")
        finish()
    }

    override fun onReq(req: BaseReq?) {
        println(req)
    }
}