package io.wexchain.android.dcc.modules.addressbook.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress

class DefaultBeneficiaryAddressVm(application: Application):AndroidViewModel(application) {

    val defaultAddr = getApplication<App>().passportRepository.defaultBeneficiaryAddress

    val walletAddr = getApplication<App>().passportRepository.currAddress

    fun changeDefault(beneficiaryAddress: BeneficiaryAddress?,checked:Boolean){
        beneficiaryAddress?.let {
            if(checked && defaultAddr.value != it.address){
                App.get().passportRepository.setDefaultBeneficiaryAddress(it.address)
            }else if(!checked && defaultAddr.value == it.address){
                App.get().passportRepository.setDefaultBeneficiaryAddress(null)
            }else{
                //noop
            }
        }
    }
}
