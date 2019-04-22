package io.wexchain.android.dcc.modules.ipfs.activity

import android.content.ClipData
import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_cloud_address.*


/**
 *Created by liuyang on 2018/8/16.
 */
class CloudAddressActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_address)
        initToolbar()
        initData()
        initClick()
    }

    private fun initClick() {
        copy_hash.onClick {
            getClipboardManager().primaryClip = ClipData.newPlainText("Hash", cloud_address_hash.text)
            toast(getString(R.string.toast_ipfs_msg7))
        }

        copy_address.onClick {
            getClipboardManager().primaryClip = ClipData.newPlainText("address", cloud_address_address.text)
            toast(getString(R.string.toast_ipfs_msg8))
        }
    }

    private fun initData() {
        val token = intent.getStringExtra("address")
        cloud_address_hash.text = token
        val url = BuildConfig.IPFS_ADDRESS
        val list = url.split(':')
        val host = list[1].substring(2, list[1].length)
        cloud_address_address.text = "http://$host:8000/ipfs/$token"
    }
}
