package worhavah.regloginlib.tools

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * Created by lulingzhi on 2017/11/21.
 */
class AutoLoadLiveData<T>(val loadUpstream: () -> Flowable<T>) : MutableLiveData<T>() {

    private var loadToken: Disposable? = null

    override fun onActive() {
        super.onActive()
        loadToken?.dispose()//todo ??
        loadToken = loadUpstream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (value != it) {
                        value = it
                    }
                }
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
                .subscribe { it ->
                    if (value != it) {
                        value = it
                    }
                }
    }
}