package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.vm.InputPasswordVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCreatePassportBinding

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
            pw ?: return@setOnClickListener
            if (CommonUtils.checkPassword(pw)) {
                doCreatePassport(pw)
            } else {
                toast("设置钱包密码不符合要求,请重试")
            }
        }
        binding.tvBackupNotice.text = SpannableString(getString(R.string.WeXCreatePassportViewController_description3)).apply {
            val drawable = ContextCompat.getDrawable(this@CreatePassportActivity, R.drawable.ic_settings)!!
            val height = binding.tvBackupNotice.lineHeight
            drawable.setBounds(0, 0, height, height)
            setSpan(ImageSpan(drawable), 16, 17, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    private fun doCreatePassport(password: String) {
        PassportOperations.createNewAndEnablePassport(password)
                .withLoading()
                .subscribe({
                    onCreateSuccess()
                }, {
                    it.printStackTrace()
                })
    }

    private fun onCreateSuccess() {
        toast("创建成功")
        ShareUtils.setBoolean("has_encrypt", false)
        navigateTo(CreateScfAccountActivity::class.java)
        finish()
    }

    private fun ensurePassportIsAbsent() {
        if (App.get().passportRepository.passportExists) {
            // post action to complete onCreate() and postpone finish
            postOnMainThread {
                toast("已存在钱包")
                finish()
            }
        }
    }
}
