package io.wexchain.android.dcc.modules.mine

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.modules.passport.PassportExportActivity
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.vm.ModifyPassword
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityModifyPassportPasswordBinding

class ModifyPassportPasswordActivity : BaseCompatActivity() {

    private lateinit var binding: ActivityModifyPassportPasswordBinding

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
        modifyPassword.newPasswdErrorEvent.observe(this, Observer {
            toast("新密码不少于8位字符，字母数字特殊符号至少2种混合")
        })
        modifyPassword.oldpasswordErrorEvent.observe(this, Observer {
            toast("原密码错误")
        })
        modifyPassword.modifyPasswordFailEvent.observe(this, Observer {
            toast(it ?: "发生错误")
        })
        modifyPassword.loadingEvent.observe(this, Observer {
            it ?: return@Observer
            if (it) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        })
        VerifyProtectFragment.serve(modifyPassword, this)
        binding.modifyPassword = modifyPassword
    }

    private fun showSucceedBackupDialog() {
        val dialog = DeleteAddressBookDialog(this)
        dialog.setTips("修改成功")
        dialog.setTvText("钱包密码修改成功,KEYSTORE信息变更,建议您立即重新备份钱包.")
        dialog.setBtnText("暂不备份", "备份钱包")
        dialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
            override fun cancel() {
                dialog.dismiss()
                toExport()
                finish()
            }

            override fun sure() {
                dialog.dismiss()
                finish()
            }
        })
        dialog.show()
    }

    private fun toExport() {
        startActivity(Intent(this@ModifyPassportPasswordActivity, PassportExportActivity::class.java))
    }
}
