package worhavah.tongniucertmodule

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import worhavah.regloginlib.tools.isPhoneNumValid

class InputPhoneInfoVm : ViewModel() {
    var phoneNo = ObservableField<String>()
    val servicePassword = ObservableField<String>()

    val certFee = ObservableField<String>()

    val submitEvent = SingleLiveEvent<Pair<String, String>>()

    fun submit() {
        val phone = phoneNo.get()
        val pw = servicePassword.get()
        if (isPhoneNumValid(phone) && !pw.isNullOrBlank()) {
            submitEvent.value = phone!! to pw!!
        }
    }

    fun clear() {
        phoneNo.set("")
        servicePassword.set("")
    }
}
