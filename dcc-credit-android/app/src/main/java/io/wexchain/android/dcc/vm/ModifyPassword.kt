package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.isPasswordValid
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.UseProtect
import io.wexchain.dcc.R

/**
 * Created by lulingzhi on 2017/11/23.
 */
class ModifyPassword(application: Application) : AndroidViewModel(application), UseProtect {

    override val type = ObservableField<LocalProtectType>()
    override val protectChallengeEvent = SingleLiveEvent<(Boolean) -> Unit>()

    private val passportRepo = App.get().passportRepository

    val inputCurrentPassword = InputPasswordVm(application).apply {
        this.passwordValidator = { isOldPasswordValid(it) }
        this.passwordHint.set(application.getString(R.string.please_input_current_passport_password))
    }
    val inputNewPassword = InputPasswordVm(application).apply {
        this.passwordHint.set(application.getString(R.string.please_input_new_passport_password))
    }

    val loadingEvent = SingleLiveEvent<Boolean>()
    val modifyPasswordSuccessEvent = SingleLiveEvent<Void>()
    val passwordCheckInvalidEvent = SingleLiveEvent<Void>()
    val modifyPasswordFailEvent = SingleLiveEvent<String>()


    fun clickModify() {
        val inputPw = inputCurrentPassword.password.get()
        val inputNewPw = inputNewPassword.password.get()
        inputPw ?: return
        inputNewPw ?: return
        if (isOldPasswordValid(inputPw) && isPasswordValid(inputNewPw)) {
            verifyProtect {
                val passport = passportRepo.getCurrentPassport()
                if (passport == null) {
                    //todo
                    return@verifyProtect
                }

                Single.just(inputNewPw)
                        .observeOn(Schedulers.io())
                        .map {
                            passportRepo.changePassword(passport, inputNewPw)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            loadingEvent.value = true
                            inputCurrentPassword.password.set("")
                            inputNewPassword.password.set("")
                        }
                        .doFinally {
                            loadingEvent.value = false
                        }
                        .subscribe({
                            modifyPasswordSuccessEvent.call()
                        }, {
                            modifyPasswordFailEvent.call()
                        })
            }
        }else{
            passwordCheckInvalidEvent.call()
        }
    }

    private fun isOldPasswordValid(pw: String?) = pw != null && pw.isNotBlank()

    fun reset() {
        inputCurrentPassword.reset()
        inputNewPassword.reset()
    }
}