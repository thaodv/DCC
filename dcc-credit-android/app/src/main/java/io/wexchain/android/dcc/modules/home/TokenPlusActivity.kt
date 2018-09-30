package io.wexchain.android.dcc.modules.home

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import io.wexchain.android.common.loadLanguageUrl
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/9/19.
 */
class TokenPlusActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tokenplus)
        initToolbar()
        loadFromWeb()
    }

    private fun loadFromWeb() {
        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.run {
            javaScriptEnabled = true
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
        webView.loadLanguageUrl("https://open.dcc.finance/dapp/tokenPlus/tokenPlus.html")
    }
}