package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.dcc.tools.SingleLiveEvent
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.dccchainservice.domain.IdOcrInfo

/**
 * Created by sisel on 2018/2/27.
 */
class EditIdCardInfoVm : ViewModel() {

    val imgFront = ObservableField<ByteArray>()
    val imgBack = ObservableField<ByteArray>()

    val name = ObservableField<String>()
    val sex = ObservableField<String>()
    val race = ObservableField<String>()
    var year: Int? = null
    var month: Int? = null
    var dayOfMonth: Int? = null
    val birth = ObservableField<String>()
    val address = ObservableField<String>()
    val idNo = ObservableField<String>()
    val timeLimit = ObservableField<String>()
    val authority = ObservableField<String>()

    val proceedEvent = SingleLiveEvent<IdCardEssentialData>()
    val informationIncompleteEvent = SingleLiveEvent<CharSequence>()


    fun setOrLoad(
        info: IdOcrInfo.IdCardInfo?
    ) {
        if (info != null) {
            setInfoFromIdCardInfo(info)
        }
    }

    private fun setInfoFromIdCardInfo(info: IdOcrInfo.IdCardInfo) {
        info.let {
            it.name?.let { name.set(it) }
            it.sex?.let { sex.set(it) }
            it.nation?.let { race.set(it) }
            it.year?.let { year = it.toIntOrNull() }
            it.month?.let { month = it.toIntOrNull() }
            it.day?.let { dayOfMonth = it.toIntOrNull() }
            it.address?.let { address.set(it) }
            it.number?.let { idNo.set(it) }
            it.timelimit?.let { timeLimit.set(it) }
            it.authority?.let { authority.set(it) }
            updateBirthText()
        }
    }

    fun confirmToNext() {
        val n = name.get()
        val id = idNo.get()
        val tl = timeLimit.get()
        if (n == null) {
            informationIncompleteEvent.value = "请填写姓名"
            return
        }
        if (id == null) {
            informationIncompleteEvent.value = "请填写身份证号"
            return
        }
        if (tl == null) {
            informationIncompleteEvent.value = "请填写有效期限"
            return
        }
        IdCardEssentialData.from(
            n,
            id,
            tl,
            sex.get(),
            race.get(),
            year, month, dayOfMonth,
            address.get(),
            authority.get()
        )?.let {
            proceedEvent.value = it
        }
    }

    private fun updateBirthText() {
        if (year != null && month != null && dayOfMonth != null) {
            this.birth.set("${year}年${month}月${dayOfMonth}日")
        }
    }

    fun toIdCardInfo(): IdOcrInfo.IdCardInfo {
        return IdOcrInfo.IdCardInfo(
            address = address.get(),
            day = dayOfMonth.toString(),
            month = month.toString(),
            year = year.toString(),
            nation = race.get(),
            sex = sex.get(),
            name = name.get(),
            number = idNo.get(),
            authority = authority.get(),
            timelimit = timeLimit.get()
        )
    }

}