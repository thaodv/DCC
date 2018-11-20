package io.wexchain.android.dcc.vm

import android.databinding.BaseObservable
import android.databinding.ObservableField

class SelectOptions : BaseObservable() {

    val optionTitle = ObservableField<String>()

    val options = ObservableField<List<String>>()

    val selected = ObservableField<String>()

    //    val onSelected
    fun onSelectionChanged(pos: Int) {
        val sel = options.get()?.getOrNull(pos)
        selected.set(sel)
    }
}