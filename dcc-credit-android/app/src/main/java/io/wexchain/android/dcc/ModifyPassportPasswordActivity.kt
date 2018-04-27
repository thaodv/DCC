package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.vm.ModifyPassword
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityModifyPassportPasswordBinding

class ModifyPassportPasswordActivity : BaseCompatActivity() {

    private lateinit var binding :ActivityModifyPassportPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_passport_password)
        initToolbar(true)
        initViewModel()
    }

    private fun initViewModel() {
        val modifyPassword = ViewModelProviders.of(this).get(ModifyPassword::class.java)
        modifyPassword.reset()
        modifyPassword.modifyPasswordSuccessEvent.observe(this, Observer {
            showSucceedBackupDialog()
        })
        modifyPassword.passwordCheckInvalidEvent.observe(this, Observer {
            toast(R.string.password_length_invalid)
        })
        modifyPassword.modifyPasswordFailEvent.observe(this, Observer {
            toast(it ?: "发生错误")
        })
        modifyPassword.loadingEvent.observe(this, Observer {
            it?:return@Observer
            if(it){
                showLoadingDialog()
            }else{
                hideLoadingDialog()
            }
        })
        VerifyProtectFragment.serve(modifyPassword, this)
        binding.modifyPassword = modifyPassword
    }

    private fun showSucceedBackupDialog() {
        CustomDialog(this)
                .apply {
                    setTitle("修改成功")
                    textContent = "钱包密码修改成功,KEYSTORE信息变更,建议您立即重新备份钱包."
                    withNegativeButton("暂不备份")
                    withPositiveButton("备份钱包") {
                        toExport()
                        true
                    }
                }
                .assembleAndShow()
    }

    private fun toExport() {
        startActivity(Intent(this@ModifyPassportPasswordActivity, PassportExportActivity::class.java))
    }
}
