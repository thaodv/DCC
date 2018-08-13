package io.wexchain.android.dcc

import android.os.Bundle
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.tools.isPasswordValid
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityOpencloudBinding

/**
 *Created by liuyang on 2018/8/13.
 */
class OpenCloudActivity : BindActivity<ActivityOpencloudBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_opencloud

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        binding.inputPw = getViewModel<InputPasswordVm>().apply {
            passwordHint.set("设置8-12位云存储密码")
            reset()
        }
        binding.btnCreatePassport.setOnClickListener {
            val pw = binding.inputPw!!.password.get()
            pw ?: return@setOnClickListener
            if (isPasswordValid(pw)) {

            } else {
                toast("设置云存储密码不符合要求,请检查")
            }
        }
        binding.tvLoadMore.onClick {
            //            CloudstorageDialog(this).createHelpDialog()
            navigateTo(ResetPasswordActivity::class.java)
        }
    }
}