package io.wexchain.android.dcc.modules.ipfs.activity

import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.domain.Function4
import io.wexchain.android.dcc.modules.ipfs.ActionType
import io.wexchain.android.dcc.modules.ipfs.EventType
import io.wexchain.android.dcc.modules.ipfs.IpfsStatus
import io.wexchain.android.dcc.modules.ipfs.service.IpfsBinder
import io.wexchain.android.dcc.modules.ipfs.service.IpfsService
import io.wexchain.android.dcc.modules.ipfs.vm.CloudItemVm
import io.wexchain.android.dcc.view.dialog.CloudstorageDialog
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyCloudBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.type.TaskCode
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.utils.io_main
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Utf8String
import worhavah.certs.tools.CertOperations.certed
import java.util.*
import java.util.concurrent.TimeUnit


/**
 *Created by liuyang on 2018/8/16.
 */
class MyCloudActivity : BindActivity<ActivityMyCloudBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_my_cloud

    private var ID_Token = ""
    private var Bank_Token = ""
    private var Cm_Token = ""
    private var Tn_Token = ""

    private lateinit var mBinder: IpfsBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        initService()
        initData()
        initClick()
    }

    private fun initVm() {
        binding.apply {
            vm = getViewModel()
            asIdVm = getViewModel("ID")
            asIdVm!!.name.set("实名认证数据")
            asBankVm = getViewModel("BANK")
            asBankVm!!.name.set("银行卡认证数据")
            asCmVm = getViewModel("CM")
            asCmVm!!.name.set("运营商通讯记录")
            asCmTnVm = getViewModel("CMTN")
            asCmTnVm!!.name.set("同牛运营商通讯记录")
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
    }

    private fun initData() {
        val idCertPassed = CertOperations.isIdCertPassed()
        val bankCertPassed = CertOperations.isBankCertPassed()
        val status = CertOperations.getCmLogUserStatus()
        val cmStatus = status == UserCertStatus.DONE || status == UserCertStatus.TIMEOUT
        val tnstatus = worhavah.certs.tools.CertOperations.getTNLogUserStatus()

        Singles.zip(
                s1 = Single.just(!idCertPassed).checkStatus(ChainGateway.BUSINESS_ID),
                s2 = Single.just(!bankCertPassed).checkStatus(ChainGateway.BUSINESS_BANK_CARD),
                s3 = Single.just(!cmStatus).checkStatus(ChainGateway.BUSINESS_COMMUNICATION_LOG),
                s4 = Single.just(tnstatus != worhavah.certs.tools.UserCertStatus.DONE).checkStatus(ChainGateway.TN_COMMUNICATION_LOG),
                zipper = ::Function4)
                .io_main()
                .withLoading()
                .subscribeBy {
                    updateUI(it)
                }
    }

    private fun updateUI(it: Function4<IpfsStatus, IpfsStatus, IpfsStatus, IpfsStatus>) {
        binding.apply {
            asIdVm!!.setItemData(it.data1, ChainGateway.BUSINESS_ID)
            asBankVm!!.setItemData(it.data2, ChainGateway.BUSINESS_BANK_CARD)
            asCmVm!!.setItemData(it.data3, ChainGateway.BUSINESS_COMMUNICATION_LOG)
            asCmTnVm!!.setItemData(it.data4, ChainGateway.TN_COMMUNICATION_LOG)
        }
        sync()
    }

    private fun CloudItemVm.setItemData(status: IpfsStatus, business: String) {
        state.set(status)
        when (status) {
            IpfsStatus.STATUS_UPLOAD, IpfsStatus.STATUS_DOWNLOAD -> action.set(ActionType.STATUS_SELECT)
            else -> action.set(ActionType.STATUS_NOSELECT)
        }
        if (status == IpfsStatus.STATUS_UPLOAD) {
            mBinder.createItemData(business)
                    .io_main()
                    .subscribeBy {
                        this.size.set(it)
                    }
        }
    }

    private fun Single<Boolean>.checkStatus(business: String): Single<IpfsStatus> {
        return this.map {
            //本地没有数据
            if (it) {
                checkIpfsAndChainDigest(business)
                        .map {
                            //true ipfs数据和链上最新一致
                            if (it) IpfsStatus.STATUS_DOWNLOAD else IpfsStatus.STATUS_RECERTIFICATION
                        }
                        .blockingGet()
            } else {//本地有数据
                val checkChainData = CertOperations.checkLocalIdAndChainData(business).blockingGet()
                if (checkChainData) {//true 本地数据和链上最新一致
                    //if (checkChainData||business.equals(ChainGateway.TN_COMMUNICATION_LOG)) {//true 本地数据和链上最新一致
                    checkIpfsAndChainDigest(business)
                            .map {
                                //true ipfs数据和链上最新一致
                                if (it) IpfsStatus.STATUS_NEWEST else IpfsStatus.STATUS_UPLOAD
                            }
                            .blockingGet()
                } else {
                    checkIpfsAndChainDigest(business)
                            .map {
                                //true ipfs数据和链上最新一致
                                if (it) IpfsStatus.STATUS_DOWNLOAD else IpfsStatus.STATUS_RECERTIFICATION
                            }.blockingGet()
                }
            }
        }
    }

    private fun checkIpfsAndChainDigest(business: String): Single<Boolean> {
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
                            ChainGateway.TN_COMMUNICATION_LOG -> {
                                Tn_Token = (it[4] as Utf8String).value
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


    private fun updateStatus(business: String, status: EventType) {
        when (business) {
            ChainGateway.BUSINESS_ID -> {
                binding.asIdVm!!.event.set(status)
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                binding.asBankVm!!.event.set(status)
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                binding.asCmVm!!.event.set(status)
            }
            ChainGateway.TN_COMMUNICATION_LOG -> {
                binding.asCmTnVm!!.event.set(status)
            }
        }
    }

    private fun successful(business: String) {
        certed()
        when (business) {
            ChainGateway.BUSINESS_ID -> {
                binding.asIdVm!!.event.set(EventType.STATUS_COMPLETE)
                Single.timer(1, TimeUnit.SECONDS)
                        .map {
                            val idCertPassed = CertOperations.isIdCertPassed()
                            !idCertPassed
                        }
                        .checkStatus(ChainGateway.BUSINESS_ID)
                        .zipWith(GardenOperations.completeTask(TaskCode.BACKUP_ID))
                        .io_main()
                        .subscribeBy {
                            binding.asIdVm!!.state.set(it.first)
                        }
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                binding.asBankVm!!.event.set(EventType.STATUS_COMPLETE)
                val bankCertPassed = CertOperations.isBankCertPassed()
                Single.just(!bankCertPassed)
                        .checkStatus(ChainGateway.BUSINESS_BANK_CARD)
                        .zipWith(GardenOperations.completeTask(TaskCode.BACKUP_BANK_CARD))
                        .io_main()
                        .subscribeBy {
                            binding.asBankVm!!.state.set(it.first)
                        }
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                binding.asCmVm!!.event.set(EventType.STATUS_COMPLETE)
                val status = CertOperations.getCmLogUserStatus()
                val cmStatus = status == UserCertStatus.DONE || status == UserCertStatus.TIMEOUT
                Single.just(!cmStatus)
                        .checkStatus(ChainGateway.BUSINESS_COMMUNICATION_LOG)
                        .zipWith(GardenOperations.completeTask(TaskCode.BACKUP_COMMUNICATION_LOG))
                        .io_main()
                        .subscribeBy {
                            binding.asCmVm!!.state.set(it.first)
                        }
            }
            ChainGateway.TN_COMMUNICATION_LOG -> {
                binding.asCmTnVm!!.event.set(EventType.STATUS_COMPLETE)
                val tnstatus = worhavah.certs.tools.CertOperations.getTNLogUserStatus()
                Single.just(tnstatus != worhavah.certs.tools.UserCertStatus.DONE)
                        .checkStatus(ChainGateway.TN_COMMUNICATION_LOG)
                        .zipWith(GardenOperations.completeTask(TaskCode.BACKUP_TN_COMMUNICATION_LOG))
                        .io_main()
                        .subscribeBy {
                            binding.asCmTnVm!!.state.set(it.first)
                        }
            }
        }
    }

    private fun initClick() {
        binding.vm!!.apply {
            tipsCall.observe(this@MyCloudActivity, Observer {
                CloudstorageDialog(this@MyCloudActivity).createTipsDialog()
            })
            resetCall.observe(this@MyCloudActivity, Observer {
                navigateTo(ResetPasswordActivity::class.java)
            })
            syncCall.observe(this@MyCloudActivity, Observer {
                binding.apply {
                    asIdVm!!.checkAction(ChainGateway.BUSINESS_ID, ID_ENTIFY_DATA)
                    asBankVm!!.checkAction(ChainGateway.BUSINESS_BANK_CARD, BANK_CARD_DATA)
                    asCmVm!!.checkAction(ChainGateway.BUSINESS_COMMUNICATION_LOG, PHONE_OPERATOR)
                    asCmTnVm!!.checkAction(ChainGateway.TN_COMMUNICATION_LOG, TNDATA)
                }
                sync()
            })
        }

        binding.asIdVm!!.setEnent { ID_Token }
        binding.asBankVm!!.setEnent { Bank_Token }
        binding.asCmVm!!.setEnent { Cm_Token }
        binding.asCmTnVm!!.setEnent { Tn_Token }
    }

    private fun CloudItemVm.setEnent(token: () -> String) {
        this.run {
            itemCall.observe(this@MyCloudActivity, Observer { sync() })
            addressCall.observe(this@MyCloudActivity, Observer {
                navigateTo(CloudAddressActivity::class.java) {
                    putExtra("address", token())
                }
            })
        }
    }

    private fun sync() {
        if (binding.asIdVm!!.checkSync() || binding.asBankVm!!.checkSync() || binding.asCmVm!!.checkSync() || binding.asCmTnVm!!.checkSync()) {
            binding.vm!!.isEnable.set(true)
        } else {
            binding.vm!!.isEnable.set(false)
        }
    }

    private fun CloudItemVm.checkSync(): Boolean {
        return action.get() == ActionType.STATUS_SELECT && event.get() == EventType.STATUS_DEFAULT
    }

    private fun CloudItemVm.checkAction(business: String, filename: String) {
        if (action.get() == ActionType.STATUS_SELECT && state.get() == IpfsStatus.STATUS_DOWNLOAD) {
            downLoadData(business, filename)
        } else if (action.get() == ActionType.STATUS_SELECT && state.get() == IpfsStatus.STATUS_UPLOAD) {
            uploadData(business, filename)
        }
    }

    private fun uploadData(business: String, filename: String) {
        mBinder.upload(business, filename,
                status = this::updateStatus,
                successful = this::successful,
                onProgress = { b, p ->
                    setProgress(b, p, EventType.STATUS_UPLOADING)
                },
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
            ChainGateway.TN_COMMUNICATION_LOG -> {
                toast("同牛认证信息上传失败")
            }
        }
    }

    private fun downLoadData(business: String, filename: String) {
        mBinder.download(business, filename,
                status = this::updateStatus,
                onSuccess = this::successful,
                onProgress = { b, p ->
                    setProgress(b, p, EventType.STATUS_DOWNLOADING)
                },
                onError = {
                    if (it is DccChainServiceException) {
                        toast(it.message!!)
                    } else {
                        toast("同步失败")
                    }
                })
    }

    private fun setProgress(business: String, progress: Int, type: EventType) {
        when (business) {
            ChainGateway.BUSINESS_ID -> {
                binding.asIdVm!!.apply {
                    this.progress.set(progress)
                    event.set(type)
                }
            }
            ChainGateway.BUSINESS_BANK_CARD -> {
                binding.asBankVm!!.apply {
                    this.progress.set(progress)
                    event.set(type)
                }
            }
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                binding.asCmVm!!.apply {
                    this.progress.set(progress)
                    event.set(type)
                }
            }
            ChainGateway.TN_COMMUNICATION_LOG -> {
                binding.asCmTnVm!!.apply {
                    this.progress.set(progress)
                    event.set(type)
                }
            }
        }
    }


    companion object {
        const val ID_ENTIFY_DATA = "identifyData"
        const val BANK_CARD_DATA = "bankCardData"
        const val PHONE_OPERATOR = "phoneOperator"
        const val TNDATA = "sameCowPhoneOperator"
    }
}