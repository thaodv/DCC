package io.wexchain.android.dcc

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R

class DccAffiliateTermsActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dcc_affiliate_terms)
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
                return shouldOverrideUrlLoadingCompat(webView,url)
            }

            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return shouldOverrideUrlLoadingCompat(webView,uri.toString())
            }

            private fun shouldOverrideUrlLoadingCompat(webView: WebView,url: String): Boolean {
                webView.loadUrl(url)
                return true // Returning True means that application wants to leave the current WebView and handle the url itself, otherwise return false.
            }
        }
        webView.loadUrl("https://open.dcc.finance/dapp/activity.html")
    }
}
