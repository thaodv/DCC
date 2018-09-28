package io.wexchain.android.dcc.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.os.SystemClock
import io.reactivex.Flowable
import io.wexchain.android.common.switchMap
import worhavah.regloginlib.tools.AutoLoadLiveData
import java.util.concurrent.TimeUnit

class VerifyBankCardSmsCodeVm : ViewModel() {
    val phoneNo = ObservableField<String>()

    val code = ObservableField<String>()

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

    companion object {
        private const val SMS_RESEND_LIMIT = 60 * 1000L
        private const val SMS_RESEND_TIME_INTERVAL = 1000L
    }
}