package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.databinding.Observable
import android.databinding.ObservableField
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.digitalwallet.DigitalCurrency
import java.util.concurrent.TimeUnit

/**
 * Created by sisel on 2018/1/25.
 */
class SearchTokenVm(application: Application) : AndroidViewModel(application) {

    val queryText = ObservableField<String>()

    val selected = SelectedDigitalCurrencies()

    private val assetsRepository = getApplication<App>().assetsRepository

    private var queryChangedEmitter: FlowableEmitter<String>? = null

    val tokens = MutableLiveData<List<DigitalCurrency>>()

    val selectedDc = Transformations.map(assetsRepository.selectedCurrencies, {
        selected.set.set(it.toSet())
        it
    })

    init {
        Flowable.create<String>({
            queryChangedEmitter = it
        }, BackpressureStrategy.LATEST)
                .debounce(500L, TimeUnit.MILLISECONDS)
                .subscribe {
                    doQuery(it)
                }
        queryText.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: Observable?, p1: Int) {
                queryChangedEmitter?.onNext(queryText.get()?:"")
            }
        })
    }

    private fun doQuery(query: String?) {
        if (query == null || query.isEmpty()) {
            Single.just(emptyList<DigitalCurrency>())
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            assetsRepository.queryTokens(query)
        }.subscribe({
            tokens.value = it
        }, {
            stackTrace(it)
        })
    }

    fun clearQuery() {
        queryText.set("")
    }

    fun addSelected(dc: DigitalCurrency?) {
        dc ?: return
        assetsRepository.addSelected(dc)
    }
}