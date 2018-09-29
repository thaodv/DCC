package worhavah.certs.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import worhavah.regloginlib.tools.ScfOperations
import java.math.RoundingMode

class CertFeeConfirmVm :ViewModel(){

    val fee=ObservableField<String>()

    val holding = ObservableField<String>()

    val confirmEvent = SingleLiveEvent<Void>()

    fun confirm(){
        confirmEvent.call()
    }

    fun loadHolding(){
        ScfOperations.loadHolding()
            .subscribe ({ value ->
                holding.set("${value.setScale(4, RoundingMode.DOWN).toPlainString()} DCC")
            },{
                it.printStackTrace()
            })
    }
}