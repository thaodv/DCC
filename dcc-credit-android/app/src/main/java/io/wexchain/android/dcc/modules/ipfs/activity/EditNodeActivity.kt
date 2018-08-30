package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import android.text.TextUtils
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.tools.Log
import io.wexchain.dcc.R
import io.wexchain.ipfs.utils.doMain
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
                toast("请填写公网地址！")
                return@onClick
            }
            if (TextUtils.isEmpty(port)) {
                toast("请填写端口号！")
                return@onClick
            }
            confirm(host, port)
        }
    }

    fun confirm(host: String, port: String) {
        val url = "http://$host:$port/api/v0/version?number=false&commit=false&repo=false&all=false"
        App.get().contractApi.getIpfsVersion(url)
                .subscribeOn(Schedulers.io())
                .doMain()
                .subscribeBy(
                        onSuccess = {
                            passport.setIpfsUrlConfig(host, port)
                            finish()
                        },
                        onError = {
                            Log(it.message)
                            toast("保存失败，该节点没有响应")
                        }
                )

    }
}