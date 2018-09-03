package io.wexchain.android.dcc.modules.ipfs.activity

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_cloud_address.*


/**
 *Created by liuyang on 2018/8/16.
 */
class CloudAddressActivity : BaseCompatActivity() {

    private val cm by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val passport by lazy {
        App.get().passportRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_address)
        initToolbar()
        initData()
        initClick()
    }

    private fun initClick() {
        copy_hash.onClick {
            cm.text = cloud_address_hash.text
            toast("复制Hash成功")
        }

        copy_address.onClick {
            cm.text = cloud_address_address.text
            toast("复制地址成功")
        }
    }

    private fun initData() {
        val token = intent.getStringExtra("address")
        cloud_address_hash.text = token
        val hostStatus = passport.getIpfsHostStatus()
        if (hostStatus) {
            val host = BuildConfig.IPFS_ADDRESS
            val list = host.split('/')
            cloud_address_address.text = "http://${list[2]}:8080/ipfs/$token"
        } else {
            val urlConfig = passport.getIpfsUrlConfig()
            cloud_address_address.text = "http://${urlConfig.first}:8080/ipfs/$token"
        }


    }
}