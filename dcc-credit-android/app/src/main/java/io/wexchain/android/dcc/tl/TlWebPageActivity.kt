package io.wexchain.android.dcc.tl

import com.xxy.maple.tllibrary.activity.TlBrowserActivity
import com.xxy.maple.tllibrary.app.TlSampleJavascript
import com.xxy.maple.tllibrary.widget.TlX5WebView

class TlWebPageActivity:TlBrowserActivity() {
    override fun getSampleJavascript(
        activity: TlBrowserActivity?,
        webView: TlX5WebView?,
        address: String?
    ): TlSampleJavascript {
        return TlWexchainJavascript(activity,webView,address)
    }


}