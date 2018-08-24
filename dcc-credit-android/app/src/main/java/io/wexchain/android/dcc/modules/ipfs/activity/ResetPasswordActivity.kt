package io.wexchain.android.dcc.modules.ipfs.activity

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.ActivityCollector
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.tools.doMain
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import kotlinx.android.synthetic.main.activity_resetpsw.*

/**
 *Created by liuyang on 2018/8/13.
 */
class ResetPasswordActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpsw)
        initToolbar()
        val protect: Protect = getViewModel()

        protect.sync(this)
        VerifyProtectFragment.serve(protect, this)

        cloud_reset.onClick {
            if (protect.type.get() == null) {
                performPassportDelete()
            } else {
                protect.verifyProtect {
                    performPassportDelete()
                }
            }
        }
    }

    private fun performPassportDelete() {
        IpfsOperations.delectedIpfsKey()
                .doMain()
                .withLoading()
                .subscribeBy {
                    ActivityCollector.getTaskActivity(1).finish()
                    finish()
                }
    }
}