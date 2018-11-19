package io.wexchain.android.dcc.tools

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.wexchain.digitalwallet.EthsTransaction

/**
 * Created by lulingzhi on 2017/11/21.
 */
class AutoLoadLiveDatawithError<T>(val loadUpstream: () -> Flowable<List<EthsTransaction>>) : MutableLiveData<List<EthsTransaction>>() {

    private var loadToken: Disposable? = null

    override fun onActive() {
        super.onActive()
        loadToken?.dispose()//todo ??
        loadToken = loadUpstream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (value != it) {
                        value = it
                    }
                },
                    {
                        value=emptyList<EthsTransaction>()
                    })
    }

    override fun onInactive() {
        super.onInactive()
        loadToken?.dispose()
        loadToken = null
    }

    fun reload() {
        loadUpstream()
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (value != it) {
                        value = it
                    }
                },
                    {
                        value=emptyList<EthsTransaction>()
                    })
    }
}
