package io.wexchain.android.dcc

import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.PassportOperations.createNewAndEnablePassport
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
    }

    private fun doCreatePassport(password: String) {
        createNewAndEnablePassport(password)
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
                toast("已存在通行证")
                finish()
            }
        }
    }
}
