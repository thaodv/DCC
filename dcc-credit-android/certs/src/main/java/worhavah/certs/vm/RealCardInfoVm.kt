package worhavah.certs.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.android.idverify.IdVerifyHelper
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.domain.IdOcrInfo
import io.wexchain.dccchainservice.domain.Result
import worhavah.certs.tools.CertOperations

/**
 * Created by sisel on 2018/2/27.
 */
class RealCardInfoVm : ViewModel() {

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
    val proceedEvent = SingleLiveEvent<IdCardEssentialData>()
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
        var tl = timeLimit.get()
        if (n == null || n.length !in 2..50) {
            informationIncompleteEvent.value = "请输入正确的姓名"
            return
        }
        if (id == null) {
            informationIncompleteEvent.value = "请填写身份证号"
            return
        }
        if (tl == null) {
           tl="20120625-20220625"
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
            CertOperations.certApi.idOcr(CertApi.uploadFilePart(imgBytes, "id.jpg", "image/jpeg"))
                    .compose(Result.checked())
                    .observeOn(AndroidSchedulers.mainThread())
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