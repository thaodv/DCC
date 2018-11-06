package io.wexchain.android.dcc.modules.home

import android.annotation.TargetApi
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.webkit.*
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.dcc.R
import io.wexchain.dcc.WxApiManager


/**
 *Created by liuyang on 2018/9/21.
 */
class GardenActivity : BaseCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tokenplus)
        initToolbar()
        loadFromWeb()
    }

    private fun loadFromWeb() {

        toolbar?.setNavigationOnClickListener {
            goBack1()
        }

        webView = findViewById(R.id.webView)
        webView.settings.run {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                return shouldOverrideUrlLoadingCompat(webView, url)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return shouldOverrideUrlLoadingCompat(webView, uri.toString())
            }

            private fun shouldOverrideUrlLoadingCompat(webView: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true // Returning True means that application wants to leave the current WebView and handle the url itself, otherwise return false.
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                this@GardenActivity.title = title
            }
        }

        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun wechatShare() {
                shareWechat()
            }
        }, "JSTest")

        webView.loadUrl("http://10.65.100.69/dcc-open/package/dapp/mysticalGarden/index.html#/Mygarden")
    }

    private fun shareWechat() {
        val wxapi = WxApiManager.wxapi.isWXAppInstalled
        if (!wxapi) {
            toast("您还未安装微信客户端")
            return
        }

        val miniProgramObj = WXMiniProgramObject()
                .apply {
                    webpageUrl = "http://open.dcc.finance/dapp/invite/index.html" // 兼容低版本的网页链接
                    miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_TEST// 正式版:0，测试版:1，体验版:2
                    userName = "gh_0d13628f5e03"
                    path = "/pages/login/login"
                }

        val msg = WXMediaMessage(miniProgramObj)
                .apply {
                    setThumbImage(BitmapFactory.decodeResource(resources, R.drawable.logo_bitexpress))
                    title = "PK吧"
                    description = "懒得写了"
                }

        val req = SendMessageToWX.Req()
                .apply {
                    transaction = buildTransaction("webpage", false)
                    message = msg
                    scene = SendMessageToWX.Req.WXSceneSession  // 目前支持会话
                }
        WxApiManager.wxapi.sendReq(req)
    }

    private fun buildTransaction(code: String, toCircle: Boolean): String {
        return "share_${code}_${System.currentTimeMillis()}_${if (toCircle) 1 else 0}"
    }

    override fun onBackPressed() {
        goBack1()
    }

    private fun goBack1() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

}