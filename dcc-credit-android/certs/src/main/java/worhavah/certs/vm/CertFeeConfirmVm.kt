package worhavah.certs.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.megvii.idcardlib.util.Util.toast
import io.wexchain.android.common.Pop.toast
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.toast
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.tools.ScfOperations
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class CertFeeConfirmVm :ViewModel(){

    val fee=ObservableField<String>()

    val holding = ObservableField<String>()

    val confirmEvent = SingleLiveEvent<Void>()

    var holdtotal  =BigDecimal("0")
    var feec  =BigDecimal("0")
    fun confirm(){
        if(holdtotal.compareTo( feec  )<0){
            toast(  "持有量不足",Networkutils.context!!)
            return
        }
        confirmEvent.call()
    }

    fun loadHolding(){
        ScfOperations.loadHolding()
            .subscribe ({ value ->
                holding.set("${value.setScale(4, RoundingMode.DOWN).toPlainString()} DCC")
                holdtotal=value.setScale(4, RoundingMode.DOWN)
            },{
                it.printStackTrace()
            })
    }
}