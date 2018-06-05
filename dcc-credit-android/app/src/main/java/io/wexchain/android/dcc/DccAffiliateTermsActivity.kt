package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
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
        webView.loadUrl("http://open.dcc.finance/dapp/activity.html")
    }
}
