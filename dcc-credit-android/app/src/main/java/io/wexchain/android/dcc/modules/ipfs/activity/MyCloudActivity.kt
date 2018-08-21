package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.ResetPasswordActivity
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_my_cloud.*

/**
 *Created by liuyang on 2018/8/16.
 */
class MyCloudActivity : BaseCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cloud)
        initToolbar()
        initClick()
    }

    private fun initClick() {
        cloud_question.onClick {
            CloudstorageDialog(this).createTipsDialog()
        }
        cloud_update.onClick {
            navigateTo(ResetPasswordActivity::class.java)
        }

    }
}