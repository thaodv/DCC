package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.ObservableField
import io.wexchain.android.dcc.tools.PickerHelper

/**
 *Created by liuyang on 2018/12/3.
 */
class AddressVm : ViewModel() {

    private val helper = PickerHelper()

    fun init(context: Context) {
        helper.init(context)
    }

    val province = ObservableField<String>()
            .apply { set("选择省份") }
    val city = ObservableField<String>()
            .apply { set("选择城市") }
    val area = ObservableField<String>()
            .apply { set("选择地区") }


    fun selectAddress() {
        helper.showPickerView { p, c, a ->
            province.set(p)
            city.set(c)
            area.set(a)
        }
    }

    fun isSelect(): Boolean {
        return !(province.get() == "选择省份" || city.get() == "选择城市" || area.get() == "选择地区")
    }

    fun getAddress(): Triple<String, String, String> {
        return Triple(province.get()!!, city.get()!!, area.get()!!)
    }
}