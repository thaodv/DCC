package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.os.SystemClock
import io.reactivex.Flowable
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.onTextChanged
import io.wexchain.android.common.switchMap
import worhavah.regloginlib.tools.AutoLoadLiveData
import java.util.concurrent.TimeUnit

/**
 *Created by liuyang on 2018/12/13.
 */
class BoundBankVm : ViewModel() {

    val name = ObservableField<String>()
    val phone = ObservableField<String>()
    val bank = ObservableField<String>()
    val code = ObservableField<String>()

    val toast = SingleLiveEvent<String>()

    val confirm = SingleLiveEvent<Void>()
    val vCode = SingleLiveEvent<Void>()

    val upTimeStamp = MutableLiveData<Long>()

    val resendRemain = upTimeStamp.switchMap {
        val start = it ?: 0L
        val interval = SMS_RESEND_TIME_INTERVAL
        AutoLoadLiveData<Long> {
            val end = start + SMS_RESEND_LIMIT
            val curr = SystemClock.uptimeMillis()
            val remaining = end - SystemClock.uptimeMillis()
            if (remaining > 0) {
                val (count, initialDelay) = (end - curr) / interval to (end - curr) % interval
                Flowable.concat(
                        Flowable.just(count + 1),
                        Flowable.intervalRange(0L, count, initialDelay, interval, TimeUnit.MILLISECONDS).map { count - it }
                )
            } else {
                Flowable.just(0)
            }

        }
    }

    fun confirm() {
        if (name.get() == null){
            toast.postValue("请输入姓名")
            return
        }
        if (bank.get() == null){
            toast.postValue("请输入银行卡号")
            return
        }
        if (phone.get() == null){
            toast.postValue("请输入手机号")
            return
        }
        if (code.get() == null){
            toast.postValue("请输入手机验证码")
            return
        }
        confirm.call()
    }

    fun getVCode() {
        val phone = phone.get()
        if (phone == null) {
            toast.postValue("请输入手机号再获取验证码")
        } else {
            code.set("")
            vCode.call()
            upTimeStamp.postValue(SystemClock.uptimeMillis())
        }
    }

    val nameWatcher = onTextChanged {
        name.set(it)
    }

    val phoneWatcher = onTextChanged {
        phone.set(it)
    }

    val bankWatcher = onTextChanged {
        bank.set(it)
    }

    val codeWatcher = onTextChanged {
        code.set(it)
    }

    companion object {
        private const val SMS_RESEND_LIMIT = 60 * 1000L
        private const val SMS_RESEND_TIME_INTERVAL = 1000L
    }

}