package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.auth.R

class AboutActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initToolbar(true)
    }
}
