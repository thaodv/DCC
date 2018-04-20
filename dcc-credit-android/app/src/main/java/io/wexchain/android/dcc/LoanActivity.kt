package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.auth.R

class LoanActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)
        setWindowExtended()
    }
}
