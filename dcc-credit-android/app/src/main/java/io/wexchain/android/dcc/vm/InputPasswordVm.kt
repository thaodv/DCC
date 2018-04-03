package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.wexchain.android.dcc.tools.SingleLiveEvent

/**
 * Created by lulingzhi on 2017/11/29.
 */
class InputPasswordVm(application: Application) : AndroidViewModel(application) {
    val password = ObservableField<String>()
    val secure = ObservableBoolean(true)
    val passwordHint = ObservableField<String>()

    val passwordNotValidEvent = SingleLiveEvent<String>()

    var passwordValidator: (String?) -> Boolean = { pw -> pw != null && pw.length in (8..20) }

    fun changeSecure() {
        secure.set(!secure.get())
    }

    fun checkPassword(hasFocus: Boolean) {
        if (!hasFocus) {
            val pw = password.get()
            if (pw != null && pw.isNotEmpty() && !passwordValidator(pw)) {
                passwordNotValidEvent.value = pw
            }
        }
    }

    fun reset() {
        password.set("")
        secure.set(true)
    }
}