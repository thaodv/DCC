package io.wexchain.android.dcc.modules.garden.activity

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
import io.wexchain.android.dcc.chain.GardenOperations
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
                GardenOperations.shareWechat(this@GardenActivity){
                    toast(it)
                }
            }
        }, "JSTest")

        webView.loadUrl("http://10.65.100.69/dcc-open/package/dapp/mysticalGarden/index.html#/Mygarden")
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