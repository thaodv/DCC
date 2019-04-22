package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import android.text.TextUtils
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.Log
import io.wexchain.dcc.R
import io.wexchain.ipfs.core.IpfsCore
import kotlinx.android.synthetic.main.activity_edit_node.*

/**
 *Created by liuyang on 2018/8/27.
 */
class EditNodeActivity : BaseCompatActivity() {

    private val passport by lazy {
        App.get().passportRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_node)
        initToolbar()
        initClick()
    }

    private fun initClick() {
        ipfs_confirm.onClick {
            val host = ipfs_host.text.toString()
            val port = ipfs_port.text.toString()
            if (TextUtils.isEmpty(host)) {
                toast(getString(R.string.toast_ipfs_msg4))
                return@onClick
            }
            if (TextUtils.isEmpty(port)) {
                toast(getString(R.string.toast_ipfs_msg5))
                return@onClick
            }
            confirm(host, port)
        }
    }

    fun confirm(host: String, port: String) {
        IpfsCore.checkVersion(host, port)
                .withLoading()
                .subscribeBy(
                        onSuccess = {
                            toast(getString(R.string.toast_msg10))
                            passport.setIpfsUrlConfig(host, port)
                            finish()
                        },
                        onError = {
                            Log(it.message)
                            toast(getString(R.string.toast_msg12))
                        }
                )
    }
}
