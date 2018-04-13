package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import io.wexchain.auth.R

class WebPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)
        initWebView()
    }

    private fun initWebView() {
        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.apply {
            javaScriptEnabled = true
        }

        val uri = intent.data
        uri?:return
        webView.loadUrl(uri.toString())
    }
}
