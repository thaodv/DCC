package io.wexchain.android.localprotect.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent

/**
 * Created by lulingzhi on 2017/11/28.
 */
class PinVm(application: Application) : AndroidViewModel(application) {

    val pin = ObservableField<String>()
    val pinCompleteEvent = SingleLiveEvent<String>()

    fun isPresent(pin: String?, index: Int): Boolean {
        return pin != null && pin.length > index
    }

    fun emit(p: Int) {
        if (p in 0..9) {
            val newValue = pin.get() + p.toString()
            val length = newValue.length
            if (length <= 6) {
                pin.set(newValue)
                if (length == 6) {
                    pinCompleteEvent.value = newValue
                }
            }
        }
    }

    fun cancel() {
        val pinValue = pin.get()
        if (pinValue != null && pinValue.isNotEmpty()) {
            pin.set(pinValue.substring(0, pinValue.length - 1))
        }
    }

    fun clear() {
        pin.set("")
    }
}