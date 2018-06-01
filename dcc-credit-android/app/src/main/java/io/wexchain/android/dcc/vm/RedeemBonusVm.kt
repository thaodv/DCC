package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.dccchainservice.domain.RedeemToken

class RedeemBonusVm:ViewModel() {

    val redeemToken = ObservableField<RedeemToken>()

    val skipEvent = SingleLiveEvent<Void>()

    val redeemEvent = SingleLiveEvent<RedeemToken>()

    val redeemCompleteEvent = SingleLiveEvent<Void>()

    fun setToken(redeemToken: RedeemToken){
        this.redeemToken.set(redeemToken)
    }

    fun skip(){
        skipEvent.call()
    }

    fun redeem(){
        redeemEvent.value = redeemToken.get()
    }

    fun complete(){
        redeemCompleteEvent.call()
    }
}