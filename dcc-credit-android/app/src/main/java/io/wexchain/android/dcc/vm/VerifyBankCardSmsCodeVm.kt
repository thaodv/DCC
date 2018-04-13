package io.wexchain.android.dcc.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableLong
import android.os.SystemClock
import io.reactivex.Flowable
import io.wexchain.android.common.switchMap
import io.wexchain.android.dcc.tools.AutoLoadLiveData
import java.util.concurrent.TimeUnit

class VerifyBankCardSmsCodeVm : ViewModel() {
    val phoneNo = ObservableField<String>()

    val code = ObservableField<String>()

    val upTimeStamp = MutableLiveData<Long>()

    val resendRemain = upTimeStamp.switchMap {
        val start = it ?: 0L
        val interval = 1000L
        AutoLoadLiveData<Long> {
            val end = start + 60 * 1000L
            val curr = SystemClock.uptimeMillis()
            val remaining = start + 60 * 1000L - SystemClock.uptimeMillis()
            if(remaining > 0){
                val (count,initialDelay) = (end - curr)/interval to (end - curr)%interval
                Flowable.intervalRange(0L,count,initialDelay,interval,TimeUnit.MILLISECONDS).map { count - it }
            }else{
                Flowable.just(0)
            }

        }
    }
}