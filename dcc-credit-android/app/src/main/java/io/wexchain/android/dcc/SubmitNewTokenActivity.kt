package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.auth.R

class SubmitNewTokenActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_new_token)
        initToolbar(true)
    }
}
