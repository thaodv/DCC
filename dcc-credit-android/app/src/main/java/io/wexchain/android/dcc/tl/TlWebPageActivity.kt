package io.wexchain.android.dcc.tl

import android.os.Bundle
import com.xxy.maple.tllibrary.activity.TlBrowserActivity
import com.xxy.maple.tllibrary.app.TlSampleJavascript
import com.xxy.maple.tllibrary.widget.TlX5WebView
import io.wexchain.android.common.setWebViewDebuggable

class TlWebPageActivity : TlBrowserActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setWebViewDebuggable()
        super.onCreate(savedInstanceState)
    }

    override fun getSampleJavascript(
            activity: TlBrowserActivity?,
            webView: TlX5WebView?,
            address: String?
    ): TlSampleJavascript {
        return TlWexchainJavascript(activity, webView, address)
    }


}
