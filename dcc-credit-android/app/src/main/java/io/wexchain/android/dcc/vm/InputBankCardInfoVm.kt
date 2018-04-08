package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.dccchainservice.domain.BankCodes
import io.wexchain.dccchainservice.domain.IdOcrInfo

class InputBankCardInfoVm(application: Application):AndroidViewModel(application){

    val idCardInfo = ObservableField<IdOcrInfo.IdCardInfo>()

    val options = SelectOptions().apply {
        optionTitle.set("开户行")
        options.set(BankCodes.banks)
    }

    val bankCardNo = ObservableField<String>()

    val phoneNum = ObservableField<String>()

    val submitEvent = SingleLiveEvent<BankCardInfo>()

    fun onSubmit(){
        val info = checkAndBuildBankCardInfo()
        if (info == null){
            //todo
        }else{
            submitEvent.value = info
        }
    }

    private fun checkAndBuildBankCardInfo(): BankCardInfo? {
        val bankCode = options.selected.get()
        val cardNo = bankCardNo.get()
        val phoneNo = phoneNum.get()
        val idInfo = idCardInfo.get()!!
        //todo check
        return BankCardInfo(
                accountName = idInfo.name!!,
                id = idInfo.number!!,
                bankCode = bankCode!!,
                bankCardNo = cardNo!!,
                phoneNo = phoneNo!!
        )
    }
}