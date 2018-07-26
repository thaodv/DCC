package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.vm.domain.IdCardCertData
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.android.idverify.IdVerifyHelper
import io.wexchain.dccchainservice.CertApi
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

    val ocrEvent = SingleLiveEvent<IdOcrInfo.IdCardInfo>()
    val ocrFailEvent = SingleLiveEvent<Throwable>()
    val ocrProcessing = SingleLiveEvent<Boolean>()
    val proceedEvent = SingleLiveEvent<IdCardCertData>()
    val informationIncompleteEvent = SingleLiveEvent<CharSequence>()

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
        if (n == null || n.length !in 2..50) {
            informationIncompleteEvent.value = "请输入正确的姓名"
            return
        }
        if (id == null) {
            informationIncompleteEvent.value = "请填写身份证号"
            return
        }
        if (tl == null) {
            informationIncompleteEvent.value = "请输入正确的有效期"
            return
        }
        val front = imgFront.get()
        val back = imgBack.get()
        if (front == null) {
            informationIncompleteEvent.value = "请扫描身份证正面"
            return
        }
        if (back == null) {
            informationIncompleteEvent.value = "请扫描身份证反面"
            return
        }
        val essentialData = IdCardEssentialData.from(
                n,
                id,
                tl,
                sex.get(),
                race.get(),
                year, month, dayOfMonth,
                address.get(),
                authority.get()
        )
        if (essentialData == null) {
            informationIncompleteEvent.value = "身份证信息不合法"
            return
        }
        proceedEvent.value = IdCardCertData(essentialData, null, front, back)
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

    fun setImage(imgBytes: ByteArray?, side: Int) {
        if (imgBytes != null) {
            when (side) {
                IdVerifyHelper.SIDE_FRONT -> {
                    imgFront.set(imgBytes)
                }
                IdVerifyHelper.SIDE_BACK -> {
                    imgBack.set(imgBytes)
                }
            }
            App.get().certApi.idOcr(CertApi.uploadFilePart(imgBytes, "id.jpg", "image/jpeg"))
                    .checkonMain()
                    .doOnSubscribe {
                        ocrProcessing.value = true
                    }
                    .doFinally {
                        ocrProcessing.value = false
                    }
                    .subscribe({ info ->
                        if (side == IdVerifyHelper.SIDE_BACK && imgBytes === imgBack.get()) {
                            if (info.side == IdOcrInfo.Side.back) {

                            } else {
                                //todo
                            }
                            setInfoFromIdCardInfo(info.idCardInfo)
                            ocrEvent.value = info.idCardInfo
                        }
                        if (side == IdVerifyHelper.SIDE_FRONT && imgBytes === imgFront.get()) {
                            if (info.side == IdOcrInfo.Side.back) {

                            } else {
                                //todo
                            }
                            setInfoFromIdCardInfo(info.idCardInfo)
                            ocrEvent.value = info.idCardInfo
                        }
                    }, {
                        if (side == IdVerifyHelper.SIDE_BACK && imgBytes === imgBack.get()) {
                            imgBack.set(null)
                        }
                        if (side == IdVerifyHelper.SIDE_FRONT && imgBytes === imgFront.get()) {
                            imgFront.set(null)
                        }
                        ocrFailEvent.value = it
                    })
        }
    }

}