package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.BaseObservable
import android.databinding.ObservableArrayList
import android.databinding.ObservableField

class SelectOptions:BaseObservable() {

    val optionTitle = ObservableField<String>()

    val options=ObservableField<List<CharSequence>>()

//    val onSelected
    fun onSelectionChanged(pos:Int){

    }
}