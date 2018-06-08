package io.wexchain.dcc

import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.wexchain.android.dcc.App

object WxApiManager {

    private const val APP_ID = BuildConfig.WECHAT_OPEN_APP_ID
    private const val APP_KEY = BuildConfig.WECHAT_OPEN_APP_KEY

    fun init() {
        wxapi.registerApp(APP_ID)
    }

    val wxapi by lazy {
        WXAPIFactory.createWXAPI(App.get(), APP_ID, false)
    }

    val isWXCircleSupported: Boolean
        get() {
            return wxapi.wxAppSupportAPI >= 0x21020001
        }
}