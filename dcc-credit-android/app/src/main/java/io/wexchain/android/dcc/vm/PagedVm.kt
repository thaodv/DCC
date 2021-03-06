package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.dccchainservice.domain.PagedList

abstract class PagedVm<T> : ViewModel() {

    val records = ObservableField<List<T>>()

    var hasDatas = ObservableField<List<T>>()

    val loadFailEvent = SingleLiveEvent<String>()

    //Check that the data is empty and return View.GONE
    val checkData = SingleLiveEvent<Int>()
            .apply {
                postValue(View.GONE)
            }

    val checkDataVisible = SingleLiveEvent<Int>()
            .apply {
                postValue(View.GONE)
            }

    private var page = 0

    fun load(page: Int, onLoadFinish: ((Int) -> Unit)? = null) {
        if (page == 0 || this.page + 1 == page) {

            loadPage(page)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        onLoadFinish?.invoke(page)
                    }
                    .subscribe({ list ->
                        mergeList(list.items, page)
                    }, {
                        loadFailEvent.value = "load fail"
                    })
        } else {
            onLoadFinish?.invoke(page)
        }
    }

    abstract fun loadPage(page: Int): Single<PagedList<T>>

    fun refresh(onLoadFinish: ((Int) -> Unit)? = null) {
        load(0, onLoadFinish)
    }

    fun loadNext(onLoadFinish: ((Int) -> Unit)? = null) {
        load(page + 1, onLoadFinish)
    }

    private fun mergeList(list: List<T>?, page: Int) {
        hasDatas.set(list)
        if (list == null) {
            if (page == 0) {
                checkData.postValue(View.VISIBLE)
                checkDataVisible.postValue(View.GONE)
            }
            return
        }
        if (page == 0) {
            records.set(list)
        } else if (this.page + 1 == page) {
            records.set((records.get() ?: emptyList()) + list)
        }
        checkData.postValue(
                if (records.get()?.isEmpty() != false) {
                    View.VISIBLE
                } else {
                    View.GONE
                })
        checkDataVisible.postValue(
                if (records.get()?.isEmpty() != false) {
                    View.GONE
                } else {
                    View.VISIBLE
                })
        this.page = page
    }
}
