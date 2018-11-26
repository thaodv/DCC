package io.wexchain.android.dcc.modules.garden.activity

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.webkit.*
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R


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
                GardenOperations.shareWechat {
                    toast(it)
                }
            }

            @JavascriptInterface
            fun pageInit(): String {
                val userinfo = App.get().userInfo!!
                val map = mapOf("token" to App.get().gardenTokenManager.gardenToken!!, "memberID" to userinfo.member.id.toString())
                return map.toJson()
            }

            @JavascriptInterface
            fun refreshToken() {
                /*val token = GardenOperations.loginWithCurrentPassport().blockingGet().second
                return token*/
                finish()
            }

        }, "BitExpress")

//        webView.loadUrl("http://10.65.100.69/garden/dist/#/Mygarden?playerID=${App.get().userInfo!!.member.id}&token=${App.get().gardenTokenManager.gardenToken!!}")
//        webView.loadUrl("http://funcstatic.bitphare.com/dapp/garden/dist/index.html#/Mygarden?playerID=${App.get().userInfo!!.member.id}&token=${App.get().gardenTokenManager.gardenToken!!}")
        webView.loadUrl("${BuildConfig.GARDEN_BASEURL}#/Mygarden?playerID=${App.get().userInfo!!.member.id}&token=${App.get().gardenTokenManager.gardenToken!!}")
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