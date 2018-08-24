package io.wexchain.android.dcc.modules.ipfs.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.ensureNewFile
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.modules.ipfs.vm.MyCloudVm
import io.wexchain.android.dcc.tools.formatSize
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyCloudBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.core.IpfsCore
import io.wexchain.ipfs.entity.BankInfo
import io.wexchain.ipfs.entity.IdInfo
import io.wexchain.ipfs.entity.PhoneInfo
import io.wexchain.ipfs.utils.*
import kotlinx.android.synthetic.main.activity_my_cloud.*
import org.jetbrains.anko.doAsync
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Utf8String
import java.io.File
import java.util.*

/**
 *Created by liuyang on 2018/8/16.
 */
class MyCloudActivity : BindActivity<ActivityMyCloudBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_my_cloud

    private val passport by lazy {
        App.get().passportRepository
    }
    private val rootpath by lazy {
        App.get().filesDir.absolutePath + File.separator + "cert" + File.separator + "id"
    }

    private var IDStatus = STATUS_DEFAULT
    private var BankStatus = STATUS_DEFAULT
    private var CmStatus = STATUS_DEFAULT

    private var ID_Token = ""
    private var Bank_Token = ""
    private var Cm_Token = ""
    private var isEnabled = mutableListOf<String>()
    private lateinit var viewMode: MyCloudVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initClick()
        initData()
        viewMode = getViewModel()
        binding.vm = viewMode
    }

    private fun initData() {
        val idCertPassed = CertOperations.isIdCertPassed()
        val bankCertPassed = CertOperations.isBankCertPassed()
        val status = CertOperations.getCmLogUserStatus()

        Singles.zip(
                Single.just(!idCertPassed).checkStatus(ChainGateway.BUSINESS_ID),
                Single.just(!bankCertPassed).checkStatus(ChainGateway.BUSINESS_BANK_CARD),
                Single.just(status != UserCertStatus.DONE).checkStatus(ChainGateway.BUSINESS_COMMUNICATION_LOG)
        )
                .subscribeOn(Schedulers.io())
                .doMain()
                .withLoading()
                .subscribeBy {
                    updateUI(it)
                }

    }

    private fun updateUI(it: Triple<Int, Int, Int>) {
        IDStatus = it.first
        BankStatus = it.second
        CmStatus = it.third
        updateIdItem()
        updateBankItem()
        updateCmItem()
    }

    fun Single<Boolean>.checkStatus(business: String): Single<Int> {
        return this.map {
            //本地没有数据
            if (it) {
                checkIpfsAndChainDigest(business)
                        .map {
                            //true ipfs数据和链上最新一致
                            if (it) STATUS_DOWNLOAD else STATUS_RECERTIFICATION
                        }
                        .blockingGet()
            } else {//本地有数据
                val checkChainData = CertOperations.checkLocalIdAndChainData(business).blockingGet()
                if (checkChainData) {//true 本地数据和链上最新一致
                    checkIpfsAndChainDigest(business)
                            .map {
                                //true ipfs数据和链上最新一致
                                if (it) STATUS_NEWEST else STATUS_UPLOAD
                            }
                            .blockingGet()
                } else {
                    checkIpfsAndChainDigest(business)
                            .map {
                                //true ipfs数据和链上最新一致
                                if (it) STATUS_DOWNLOAD else STATUS_RECERTIFICATION
                            }.blockingGet()
                }
            }
        }
    }

    private fun checkStatusTxt(status: Int): String {
        return when (status) {
            STATUS_DOWNLOAD -> "可下载"
            STATUS_RECERTIFICATION -> "无可上传下载数据，建议重新认证"
            STATUS_NEWEST -> "本地和云端数据均为最新数据"
            STATUS_UPLOAD -> "可上传"
            else -> "状态错误"
        }
    }

    private fun updateIdItem() {
        with(viewMode) {
            idsize.set(IDStatus == STATUS_UPLOAD)
            idselected.set(IDStatus == STATUS_UPLOAD || IDStatus == STATUS_DOWNLOAD)
            idaddress.set(IDStatus == STATUS_NEWEST)
            idtag.set(if (IDStatus == STATUS_UPLOAD || IDStatus == STATUS_DOWNLOAD) R.drawable.my_cloud_tag_selector else R.drawable.cloud_item_noselected)
            idstatus.set(checkStatusTxt(IDStatus))
            idAddressEvent.observe(this@MyCloudActivity, Observer {
                if (IDStatus == STATUS_NEWEST) {
                    navigateTo(CloudAddressActivity::class.java) {
                        putExtra("address", ID_Token)
                    }
                }
            })
        }

        if (IDStatus == STATUS_UPLOAD) {
            doAsync {
                createIdData()
            }
        }
    }

    private fun updateCmItem() {
        with(viewMode) {
            cmsize.set(CmStatus == STATUS_UPLOAD)
            cmselected.set(CmStatus == STATUS_UPLOAD || CmStatus == STATUS_DOWNLOAD)
            cmaddress.set(CmStatus == STATUS_NEWEST)
            cmtag.set(if (CmStatus == STATUS_UPLOAD || CmStatus == STATUS_DOWNLOAD) R.drawable.my_cloud_tag_selector else R.drawable.cloud_item_noselected)
            cmstatus.set(checkStatusTxt(CmStatus))
            cmAddressEvent.observe(this@MyCloudActivity, Observer {
                if (CmStatus == STATUS_NEWEST) {
                    navigateTo(CloudAddressActivity::class.java) {
                        putExtra("address", Cm_Token)
                    }
                }
            })
        }

        if (CmStatus == STATUS_UPLOAD) {
            doAsync {
                createCmData()
            }
        }
    }

    private fun updateBankItem() {
        with(viewMode) {
            banksize.set(BankStatus == STATUS_UPLOAD)
            bankselected.set(BankStatus == STATUS_UPLOAD || BankStatus == STATUS_DOWNLOAD)
            bankaddress.set(BankStatus == STATUS_NEWEST)
            banktag.set(if (BankStatus == STATUS_UPLOAD || BankStatus == STATUS_DOWNLOAD) R.drawable.my_cloud_tag_selector else R.drawable.cloud_item_noselected)
            bankstatus.set(checkStatusTxt(BankStatus))
            bankAddressEvent.observe(this@MyCloudActivity, Observer {
                if (BankStatus == STATUS_NEWEST) {
                    navigateTo(CloudAddressActivity::class.java) {
                        putExtra("address", Bank_Token)
                    }
                }
            })
        }

        if (BankStatus == STATUS_UPLOAD) {
            doAsync {
                createBankData()
            }
        }
    }

    fun checkIpfsAndChainDigest(business: String): Single<Boolean> {
        return IpfsOperations.getIpfsToken(business)
                .map {
                    FunctionReturnDecoder.decode(it.result, Erc20Helper.dncodeResponse())
                }
                .map {
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            ID_Token = (it[5] as Utf8String).value
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            Bank_Token = (it[5] as Utf8String).value
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            Cm_Token = (it[5] as Utf8String).value
                        }
                    }

                    val digest1 = (it[6] as DynamicBytes).value
                    val digest2 = (it[7] as DynamicBytes).value
                    Pair(digest1, digest2)
                }
                .map {
                    val chainDigest = CertOperations.getChainDigest(business).blockingGet()
                    if (chainDigest.first.isEmpty() && it.first.isEmpty()) {
                        false
                    } else {
                        (Arrays.equals(chainDigest.first, it.first) && Arrays.equals(chainDigest.second, it.second))
                    }
                }
    }

    private fun createIdData() {
        val certIdPics = CertOperations.getCertIdPics()
        val positivePhoto = certIdPics!!.first.base64()
        val backPhoto = certIdPics.second.base64()
        val facePhoto = certIdPics.third.base64()

        val certIdData = CertOperations.getCertIdData()!!
        val orderid = CertOperations.getOrderId()
        val idCertPassed = CertOperations.getCertIdStatus()
        val similarity = CertOperations.getCertIdSimilarity()!!
        val expiredText = ViewModelHelper.expiredText(certIdData.expired)

        val idInfo = IdInfo(
                positivePhoto = positivePhoto,
                backPhoto = backPhoto,
                facePhoto = facePhoto,
                idAuthenStatus = idCertPassed,
                orderId = orderid.toInt(),
                similarity = similarity.toFloat().toInt(),
                userAddress = certIdData.address!!,
                userName = certIdData.name,
                userNumber = certIdData.id,
                userNation = certIdData.race!!,
                userSex = certIdData.sex!!,
                userTimeLimit = expiredText,
                userAuthority = certIdData.authority!!,
                userYear = certIdData.year!!,
                userMonth = certIdData.month!!,
                userDay = certIdData.dayOfMonth!!)

        val data = idInfo.toJson().toByteArray()

        val size = getDataFromSize(data, ID_ENTIFY_DATA)
        runOnUiThread {
            viewMode.idsizetxt.set(size)
        }
    }

    fun createBankData() {
        val info = CertOperations.getCertBankCardData()!!
        val expired = CertOperations.getBankCardCertExpired()
        val bankStatus = CertOperations.getBankStatus()
        val certIdData = CertOperations.getCertIdData()!!

        val bankInfo = BankInfo(
                bankAuthenStatus = bankStatus.first,
                bankAuthenOrderid = bankStatus.second.toInt(),
                bankAuthenName = certIdData.name,
                bankAuthenCode = info.bankCode,
                bankAuthenCodeNumber = info.bankCardNo,
                bankAuthenMobile = info.phoneNo,
                bankAuthenExpired = ViewModelHelper.expiredText(expired))

        val data = bankInfo.toJson().toByteArray()

        val size = getDataFromSize(data, BANK_CARD_DATA)
        runOnUiThread {
            viewMode.banksizetxt.set(size)
        }
    }

    fun createCmData() {
        val cmCertOrderId = CertOperations.getCmCertOrderId()
        val cmLogPhoneNo = CertOperations.getCmLogPhoneNo()
        val status = CertOperations.getCmLogUserStatus().name
        val json = CertOperations.getCmLogData(cmCertOrderId)
                .map {
                    it.toString()
                }
                .blockingGet()

        val phoneInfo = PhoneInfo(
                mobileAuthenStatus = status,
                mobileAuthenOrderid = cmCertOrderId.toInt(),
                mobileAuthenNumber = cmLogPhoneNo!!,
                mobileAuthenCmData = json)

        val data = phoneInfo.toJson().toByteArray()
        val size = getDataFromSize(data, PHONE_OPERATOR)
        runOnUiThread {
            viewMode.cmsizetxt.set(size)
        }
    }

    private fun getDataFromSize(data: ByteArray, filename: String): String {
        val ipfsKeyHash = passport.getIpfsKeyHash()!!
        AES256.encrypt(data, ipfsKeyHash)
                .writeToFile(rootpath, "$filename.txt")
                .apply {
                    ZipUtil.zip(rootpath + File.separator + "$filename.zip", this)
                }
        val file = File(rootpath, "$filename.zip")
        return file.formatSize()
    }


    private fun downLoadData(business: String, filename: String) {
        IpfsOperations.getIpfsToken(business)
                .map {
                    FunctionReturnDecoder.decode(it.result, Erc20Helper.dncodeResponse())
                }
                .map {
                    (it[5] as Utf8String).value
                }
                .flatMap {
                    IpfsCore.download(it)
                }
                .map {
                    File(rootpath, "$filename.zip").ensureNewFile()
                    val zipfile = it.writeToFile(rootpath, "$filename.zip")
                    ZipUtil.unZip(zipfile, rootpath)
                    val file = File(rootpath, "$filename.txt")
                    val ipfsKeyHash = passport.getIpfsKeyHash()
                    AES256.decrypt(file.readBytes(), ipfsKeyHash!!)

                }
                .doOnSuccess {
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            val idInfo = String(it).toBean(IdInfo::class.java)
                            CertOperations.saveIpfsIdData(idInfo)
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            val bankInfo = String(it).toBean(BankInfo::class.java)
                            CertOperations.saveIpfsBankData(bankInfo)
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            val phoneInfo = String(it).toBean(PhoneInfo::class.java)
                            CertOperations.saveIpfsCmData(phoneInfo)
                        }
                        else -> {
                        }
                    }
                }
                .doMain()
                .subscribeBy {
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            cloud_id_selected.text = "已完成"
                            IDStatus = STATUS_DEFAULT
                            isEnabled.remove(ID_ENTIFY_DATA)
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            cloud_bank_selected.text = "已完成"
                            BankStatus = STATUS_DEFAULT
                            isEnabled.remove(BANK_CARD_DATA)
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            cloud_cm_selected.text = "已完成"
                            CmStatus = STATUS_DEFAULT
                            isEnabled.remove(PHONE_OPERATOR)
                        }
                    }
                    updateSyncStatus()
                }
    }

    fun uploadData(business: String, filename: String) {
        IpfsCore.upload(rootpath + File.separator + "$filename.zip")
                .flatMap {
                    val idDigest = when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            CertOperations.getLocalIdDigest()
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            CertOperations.getLocalBankDigest()
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            CertOperations.getLocalCmDigest()
                        }
                        else -> null
                    }
                    IpfsOperations.putIpfsToken(business, it, idDigest!!.first, idDigest.second)
                }
                .doMain()
                .subscribeBy {
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            cloud_id_selected.text = "已完成"
                            IDStatus = STATUS_DEFAULT
                            isEnabled.remove(ID_ENTIFY_DATA)
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            cloud_bank_selected.text = "已完成"
                            BankStatus = STATUS_DEFAULT
                            isEnabled.remove(BANK_CARD_DATA)
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            cloud_cm_selected.text = "已完成"
                            CmStatus = STATUS_DEFAULT
                            isEnabled.remove(PHONE_OPERATOR)
                        }
                    }
                    updateSyncStatus()
                }
    }


    private fun initClick() {
        cloud_question.onClick {
            CloudstorageDialog(this).createTipsDialog()
        }
        cloud_update.onClick {
            navigateTo(ResetPasswordActivity::class.java)
        }
        cloud_sync.onClick {
            if (cloud_item_id.isSelected) {
                if (IDStatus == STATUS_UPLOAD) {
                    uploadData(ChainGateway.BUSINESS_ID, ID_ENTIFY_DATA)
                } else if (IDStatus == STATUS_DOWNLOAD) {
                    downLoadData(ChainGateway.BUSINESS_ID, ID_ENTIFY_DATA)
                }
            }

            if (cloud_item_bank.isSelected) {
                if (BankStatus == STATUS_UPLOAD) {
                    uploadData(ChainGateway.BUSINESS_BANK_CARD, BANK_CARD_DATA)
                } else if (BankStatus == STATUS_DOWNLOAD) {
                    downLoadData(ChainGateway.BUSINESS_BANK_CARD, BANK_CARD_DATA)
                }
            }

            if (cloud_item_cm.isSelected) {
                if (CmStatus == STATUS_UPLOAD) {
                    uploadData(ChainGateway.BUSINESS_COMMUNICATION_LOG, PHONE_OPERATOR)
                } else if (CmStatus == STATUS_DOWNLOAD) {
                    downLoadData(ChainGateway.BUSINESS_COMMUNICATION_LOG, PHONE_OPERATOR)
                }
            }

        }

        cloud_item_id.onClick {
            if (cloud_item_id.isSelected) {
                cloud_id_selected.text = "未选中"
                isEnabled.remove(ID_ENTIFY_DATA)
            } else if (!cloud_item_id.isSelected && IDStatus == STATUS_UPLOAD) {
                cloud_id_selected.text = "等待上传"
                isEnabled.add(ID_ENTIFY_DATA)
            } else if (!cloud_item_id.isSelected && IDStatus == STATUS_DOWNLOAD) {
                cloud_id_selected.text = "等待下载"
                isEnabled.add(ID_ENTIFY_DATA)
            }
            cloud_item_id.isSelected = !cloud_item_id.isSelected
            updateSyncStatus()
        }

        cloud_item_bank.onClick {
            if (cloud_item_bank.isSelected) {
                cloud_bank_selected.text = "未选中"
                isEnabled.remove(BANK_CARD_DATA)
            } else if (!cloud_item_bank.isSelected && BankStatus == STATUS_UPLOAD) {
                cloud_bank_selected.text = "等待上传"
                isEnabled.add(BANK_CARD_DATA)
            } else if (!cloud_item_bank.isSelected && BankStatus == STATUS_DOWNLOAD) {
                cloud_bank_selected.text = "等待下载"
                isEnabled.add(BANK_CARD_DATA)
            }
            cloud_item_bank.isSelected = !cloud_item_bank.isSelected
            updateSyncStatus()
        }

        cloud_item_cm.onClick {
            if (cloud_item_cm.isSelected) {
                cloud_cm_selected.text = "未选中"
                isEnabled.remove(PHONE_OPERATOR)
            } else if (!cloud_item_cm.isSelected && CmStatus == STATUS_UPLOAD) {
                cloud_cm_selected.text = "等待上传"
                isEnabled.add(PHONE_OPERATOR)
            } else if (!cloud_item_cm.isSelected && CmStatus == STATUS_DOWNLOAD) {
                cloud_cm_selected.text = "等待下载"
                isEnabled.add(PHONE_OPERATOR)
            }
            cloud_item_cm.isSelected = !cloud_item_cm.isSelected
            updateSyncStatus()
        }
    }

    private fun updateSyncStatus() {
        cloud_sync.isEnabled = isEnabled.size > 0
    }

    companion object {
        private const val STATUS_DEFAULT = 0
        private const val STATUS_UPLOAD = 1
        private const val STATUS_RECERTIFICATION = 2
        private const val STATUS_DOWNLOAD = 3
        private const val STATUS_NEWEST = 4

        private const val ID_ENTIFY_DATA = " identifyData"
        private const val BANK_CARD_DATA = " bankCardData"
        private const val PHONE_OPERATOR = " phoneOperator"
    }
}