package io.wexchain.android.dcc.modules.ipfs.activity

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_cloud_address.*


/**
 *Created by liuyang on 2018/8/16.
 */
class CloudAddressActivity : BaseCompatActivity() {

    private val cm by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
            toast("复制成功")
        }

        copy_address.onClick {
            cm.text = cloud_address_address.text
            toast("复制成功")
        }
    }

    private fun initData() {
        val token = intent.getStringExtra("address")
        cloud_address_hash.text = token
        cloud_address_address.text = "https://ipfs.io/ipfs/$token"

    }
}