package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.repo.db.QueryHistory

class QueryBookAddressVm(application: Application) : AndroidViewModel(application) {

    open fun getRes(name: String): LiveData<List<BeneficiaryAddress>> {
        return getApplication<App>().passportRepository.queryBookAddress(name)
    }

    open fun getHistory(): LiveData<List<QueryHistory>>{
        return  getApplication<App>().passportRepository.getAddressBookQueryHistory()
    }


}
