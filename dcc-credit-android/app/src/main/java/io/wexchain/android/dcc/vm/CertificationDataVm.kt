package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.graphics.Bitmap
import io.wexchain.android.common.SingleLiveEvent

class CertificationDataVm : ViewModel() {
    val certProvider = ObservableField<String>()
    val title1 = ObservableField<String>()
    val title2 = ObservableField<String>()
    val title3 = ObservableField<String>()
    val value1 = ObservableField<String>()
    val value2 = ObservableField<String>()
    val value3 = ObservableField<String>()
    val imgFront = ObservableField<ByteArray>()
    val imgBack = ObservableField<ByteArray>()
    val imgPhoto = ObservableField<ByteArray>()

    val renewEvent = SingleLiveEvent<Void>()
    val showDataEvent = SingleLiveEvent<Void>()

    fun renew() {
        renewEvent.call()
    }

    fun showData() {
        showDataEvent.call()
    }
}