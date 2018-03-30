package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.tools.navigateTo
import io.wexchain.auth.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        findViewById<View>(R.id.card_my_credit)?.setOnClickListener {
            navigateTo(MyCreditActivity::class.java)
        }
    }
}
