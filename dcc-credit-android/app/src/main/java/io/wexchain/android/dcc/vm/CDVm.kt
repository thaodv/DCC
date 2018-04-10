package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField

/**
 * Created by lulingzhi on 2017/11/29.
 * custom dialog vm
 */
class CDVm:ViewModel() {

    val showTitle = ObservableBoolean(true)
    val showText = ObservableBoolean(true)
    val showButton1 = ObservableBoolean(true)
    val showButton2 = ObservableBoolean()
    val showButton3 = ObservableBoolean()

    val textTitle = ObservableField<CharSequence>()
    val textContent = ObservableField<CharSequence>()
    val textButton1 = ObservableField<CharSequence>()
    val textButton2 = ObservableField<CharSequence>()
    val textButton3 = ObservableField<CharSequence>()
}