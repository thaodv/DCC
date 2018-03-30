package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.wexchain.android.dcc.tools.navigateTo
import io.wexchain.auth.R

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
    }

    override fun onStart() {
        super.onStart()

        navigateTo(HomeActivity::class.java)
    }
}
