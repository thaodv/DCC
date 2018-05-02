package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.dccchainservice.domain.Bank

class InputBankCardInfoVm(application: Application):AndroidViewModel(application){

    val bank = ObservableField<Bank>()

    val bankCardNo = ObservableField<String>()

    val phoneNum = ObservableField<String>()

    val certFee = ObservableField<String>()

    val submitEvent = SingleLiveEvent<BankCardInfo>()

    val informationIncompleteEvent = SingleLiveEvent<CharSequence>()

    fun onSubmit(){
        val info = checkAndBuildBankCardInfo()
        if (info == null){
            //todo
            informationIncompleteEvent.value = ""
        }else{
            submitEvent.value = info
        }
    }

    private fun checkAndBuildBankCardInfo(): BankCardInfo? {
        val bankCode = bank.get()?.bankCode
        val cardNo = bankCardNo.get()
        val phoneNo = phoneNum.get()
        //todo check
        return if (bankCode!=null && cardNo!=null && phoneNo!=null) {
            BankCardInfo(
                    bankCode = bankCode,
                    bankCardNo = cardNo,
                    phoneNo = phoneNo
            )
        }else{
            null
        }
    }
}