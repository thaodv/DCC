package io.wexchain.android.dcc

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.atLeastCreated
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.dcc.R
import java.util.concurrent.TimeUnit

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()
        setContentView(R.layout.activity_loading)
        permission()
    }

    private fun permission() {
        RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe {
                    delayedStart()
                }
    }

    private fun delayedStart() {
        Single.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _->
                    atLeastCreated {
                        navigateTo(HomeActivity::class.java)
                    }
                }
    }
}
