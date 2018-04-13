package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.MultiChainHelper

class CertFeeConfirmVm :ViewModel(){

    val fee=ObservableField<String>()

    val holding = ObservableField<String>()

    val confirmEvent = SingleLiveEvent<Void>()

    fun confirm(){
        confirmEvent.call()
    }

    fun loadHolding(){
        val passport = App.get().passportRepository.getCurrentPassport()
        if(passport!=null){
            val dccPrivate = MultiChainHelper.getDccPrivate()
            App.get().assetsRepository.getDigitalCurrencyAgent(dccPrivate)
                    .getBalanceOf(passport.address)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { value ->
                        holding.set("${dccPrivate.toDecimalAmount(value).currencyToDisplayStr()} DCC")
                    }
        }
    }
}