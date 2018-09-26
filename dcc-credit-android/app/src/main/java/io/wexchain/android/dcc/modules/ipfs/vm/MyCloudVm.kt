package io.wexchain.android.dcc.modules.ipfs.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent

/**
 *Created by liuyang on 2018/8/23.
 */
class MyCloudVm : ViewModel() {

    val idsize = ObservableField<Boolean>()
    val idselected = ObservableField<Boolean>()
    val idaddress = ObservableField<Boolean>()
    val idstatus = ObservableField<String>()
    val idsizetxt = ObservableField<String>()
    val idtag = ObservableField<Int>()
    val idAddressEvent = SingleLiveEvent<Void>()

    val banksize = ObservableField<Boolean>()
    val bankselected = ObservableField<Boolean>()
    val bankaddress = ObservableField<Boolean>()
    val bankstatus = ObservableField<String>()
    val banksizetxt = ObservableField<String>()
    val banktag = ObservableField<Int>()
    val bankAddressEvent = SingleLiveEvent<Void>()

    val cmsize = ObservableField<Boolean>()
    val cmselected = ObservableField<Boolean>()
    val cmaddress = ObservableField<Boolean>()
    val cmstatus = ObservableField<String>()
    val cmsizetxt = ObservableField<String>()
    val cmtag = ObservableField<Int>()
    val cmAddressEvent = SingleLiveEvent<Void>()


    val tnsize = ObservableField<Boolean>()
    val tnselected = ObservableField<Boolean>()
    val tnaddress = ObservableField<Boolean>()
    val tnstatus = ObservableField<String>()
    val tnsizetxt = ObservableField<String>()
    val tntag = ObservableField<Int>()
    val tnAddressEvent = SingleLiveEvent<Void>()


    fun idAddressCall() {
        idAddressEvent.call()
    }

    fun bankAddressCall() {
        bankAddressEvent.call()
    }

    fun cmAddressCall() {
        cmAddressEvent.call()
    }

    fun tnAddressCall() {
        cmAddressEvent.call()
    }

}