package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/8/13.
 */
class ResetPasswordActivity:BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpsw)
        initToolbar()
    }
}