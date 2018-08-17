package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/8/16.
 */
class CloudAddressActivity:BaseCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_address)
        initToolbar()
    }
}