package io.wexchain.android.dcc.modules.redpacket

import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.dcc.R

class RuleActivity : BaseCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule)
        initToolbar(true)

        this.findViewById<Button>(R.id.bt_invite).setOnClickListener {
            GardenOperations.shareWechatRedPacket {
                toast(it)
            }
        }

        var mContent = this.findViewById<WebView>(R.id.wv_content)
        val settings = mContent.settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContent.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        mContent.isDrawingCacheEnabled = true
        settings.javaScriptEnabled = true
        // 取消滚动条
        mContent.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        // 触摸焦点起作用
        mContent.requestFocus()
        // 不弹窗浏览器是否保存密码
        settings.savePassword = false
        settings.defaultTextEncodingName = "utf-8"
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        // 自动适应屏幕尺寸
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS

        //支持所有标签
        settings.domStorageEnabled = true
        //mContent.addJavascriptInterface(new JavaScriptObjectUtils(this), "js");
        mContent.loadUrl("https://static.bitphare.com/dapp/redPacket/rules.html")

    }
}
