package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.tools.isPasswordValid
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityCreatePassportBinding

class CreatePassportActivity : BindActivity<ActivityCreatePassportBinding>() {
    override val contentLayoutId: Int = R.layout.activity_create_passport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        ensurePassportIsAbsent()

        binding.inputPw = getViewModel<InputPasswordVm>().apply {
            passwordHint.set(getString(R.string.please_set_passport_password))
            reset()
        }
        binding.btnCreatePassport.setOnClickListener {
            val pw = binding.inputPw!!.password.get()
            pw?:return@setOnClickListener
            if(isPasswordValid(pw)) {
                doCreatePassport(pw)
            }else{
                toast("")
            }
        }
        binding.tvBackupNotice.text = SpannableString("成功创建数字钱包后,请即时在设置->备份数字钱包 中进行备份,以防数字资产丢失").apply {
            val drawable = ContextCompat.getDrawable(this@CreatePassportActivity, R.drawable.ic_settings)!!
            val height = binding.tvBackupNotice.lineHeight
            drawable.setBounds(0,0,height,height)
            setSpan(ImageSpan(drawable),16,17,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    private fun doCreatePassport(password: String) {
        PassportOperations.createNewAndEnablePassport(password)
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    //todo
                    onCreateSuccess()
                },{
                    it.printStackTrace()
                })
    }

    private fun onCreateSuccess() {
        toast("创建成功")
        finish()
        navigateTo(PassportCreationSucceedActivity::class.java)
    }

    private fun ensurePassportIsAbsent() {
        if (App.get().passportRepository.passportExists){
            // post action to complete onCreate() and postpone finish
            postOnMainThread {
                toast("已存在钱包")
                finish()
            }
        }
    }
}
