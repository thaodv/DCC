package io.wexchain.android.dcc

import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.auth.R

class HomeActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()

        setContentView(R.layout.activity_home)
        findViewById<View>(R.id.card_my_credit)?.setOnClickListener {
            navigateTo(MyCreditActivity::class.java)
        }
    }
}
