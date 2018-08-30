package io.wexchain.android.dcc

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.atLeastCreated
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.dcc.tools.PermissionHelper
import io.wexchain.dcc.R
import java.util.concurrent.TimeUnit

class LoadingActivity : AppCompatActivity() {

    private lateinit var helper: PermissionHelper
    private val WRITE_EXTERNAL_STORAGE_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()
        setContentView(R.layout.activity_loading)
        permission()
    }

    private fun permission() {
        helper = PermissionHelper(this)
        helper.requestPermissions(arrayOf(PermissionHelper.PermissionModel("存储空间", Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "我们需要您允许我们读写你的存储卡，以方便我们临时保存一些数据", WRITE_EXTERNAL_STORAGE_CODE))) {
            delayedStart()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.onActivityResult(requestCode, resultCode, data)
    }

    private fun delayedStart() {
        Single.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    atLeastCreated {
                        navigateTo(HomeActivity::class.java)
                    }
                }
    }
}
