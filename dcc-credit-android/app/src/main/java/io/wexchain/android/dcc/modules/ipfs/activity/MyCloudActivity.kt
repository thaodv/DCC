package io.wexchain.android.dcc.modules.ipfs.activity

import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.modules.ipfs.service.IpfsBinder
import io.wexchain.android.dcc.modules.ipfs.service.IpfsService
import io.wexchain.android.dcc.modules.ipfs.vm.MyCloudVm
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyCloudBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.utils.doMain
import kotlinx.android.synthetic.main.activity_my_cloud.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Utf8String
import java.util.*


/**
 *Created by liuyang on 2018/8/16.
 */
class MyCloudActivity : BindActivity<ActivityMyCloudBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_my_cloud

    private var IDStatus = STATUS_DEFAULT
    private var BankStatus = STATUS_DEFAULT
    private var CmStatus = STATUS_DEFAULT

    private var ID_Token = ""
    private var Bank_Token = ""
    private var Cm_Token = ""
    private var isEnabled = mutableListOf<String>()
    private lateinit var viewMode: MyCloudVm
    private lateinit var mBinder: IpfsBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initClick()
        initData()
        initService()
        viewMode = getViewModel()
        binding.vm = viewMode
    }

    private fun initService() {
        val intent = Intent(this, IpfsService::class.java)
        startService(intent)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mBinder = iBinder as IpfsBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_mycloud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.mycloud_select_node -> {
                navigateTo(SelectNodeActivity::class.java)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }*/

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
                mBinder.createIdData { size ->
                    uiThread {
                        viewMode.idsizetxt.set(size)
                    }
                }
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
                mBinder.createCmData { size ->
                    uiThread {
                        viewMode.cmsizetxt.set(size)
                    }
                }
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
                mBinder.createBankData { size ->
                    uiThread {
                        viewMode.banksizetxt.set(size)
                    }
                }
            }
        }
    }

    fun checkIpfsAndChainDigest(business: String): Single<Boolean> {
        return IpfsOperations.getIpfsToken(business)
                .map {
                    if (it.result.toString() == "0x") {
                        arrayListOf()
                    } else {
                        FunctionReturnDecoder.decode(it.result, Erc20Helper.decodeTokenResponse())
                    }
                }
                .map {
                    val digest1: ByteArray
                    val digest2: ByteArray
                    if (it.size == 0) {
                        digest1 = byteArrayOf()
                        digest2 = byteArrayOf()
                    } else {
                        when (business) {
                            ChainGateway.BUSINESS_ID -> {
                                ID_Token = (it[4] as Utf8String).value
                            }
                            ChainGateway.BUSINESS_BANK_CARD -> {
                                Bank_Token = (it[4] as Utf8String).value
                            }
                            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                                Cm_Token = (it[4] as Utf8String).value
                            }
                        }
                        digest1 = (it[6] as DynamicBytes).value
                        digest2 = (it[7] as DynamicBytes).value
                    }
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


    private fun updateStatus(business: String, status: String) {
        when (business) {
            ChainGateway.BUSINESS_ID -> {
                cloud_id_selected.text = status
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                cloud_bank_selected.text = status
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                cloud_cm_selected.text = status
            }
            else -> {
            }
        }
    }

    private fun successful(business: String) {
        when (business) {
            ChainGateway.BUSINESS_ID -> {
                cloud_id_selected.text = "已完成"
                IDStatus = STATUS_DEFAULT
                cloud_item_id.isClickable = false
                isEnabled.remove(ID_ENTIFY_DATA)
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                cloud_bank_selected.text = "已完成"
                cloud_item_bank.isClickable = false
                BankStatus = STATUS_DEFAULT
                isEnabled.remove(BANK_CARD_DATA)
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                cloud_cm_selected.text = "已完成"
                CmStatus = STATUS_DEFAULT
                cloud_item_cm.isClickable = false
                isEnabled.remove(PHONE_OPERATOR)
            }
        }
        updateSyncStatus()
    }

    private fun initClick() {
        cloud_question.onClick {
            CloudstorageDialog(this).createTipsDialog()
        }
        cloud_update.onClick {
            navigateTo(ResetPasswordActivity::class.java)
        }
        cloud_sync.onClick {
            cloud_sync.isEnabled = false
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

    private fun uploadData(business: String, filename: String) {
        mBinder.upload(business, filename,
                status = this::updateStatus,
                successful = this::successful,
                onProgress = this::setProgress,
                onError = this::errorStatus)
    }

    private fun errorStatus(b: String, t: Throwable) {
        when (b) {
            ChainGateway.BUSINESS_ID -> {
                toast("实名认证信息上传失败")
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                toast("银行卡认证信息上传失败")
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                toast("运营商认证信息上传失败")
            }
        }
    }

    private fun downLoadData(business: String, filename: String) {
        mBinder.download(business, filename,
                status = this::updateStatus,
                onSuccess = this::successful,
                onProgress = this::setProgress,
                onError = {
                    if (it is DccChainServiceException) {
                        toast(it.message!!)
                    } else {
                        toast("同步失败")
                    }
                })
    }

    private fun setProgress(business: String, progress: Int) {
        when (business) {
            ChainGateway.BUSINESS_ID -> {
                cloud_id_progress.visibility = View.VISIBLE
                cloud_id_progress.progress = progress
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                cloud_bank_progress.visibility = View.VISIBLE
                cloud_bank_progress.progress = progress
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                cloud_cm_progress.visibility = View.VISIBLE
                cloud_cm_progress.progress = progress
            }
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

        const val ID_ENTIFY_DATA = "identifyData"
        const val BANK_CARD_DATA = "bankCardData"
        const val PHONE_OPERATOR = "phoneOperator"
    }
}