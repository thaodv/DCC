package io.wexchain.android.dcc.chain

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.WorkerThread
import android.support.v4.app.FragmentManager
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.Prefs
import io.wexchain.android.common.ensureNewFile
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.pair
import io.wexchain.android.dcc.view.dialog.CertFeeConfirmDialog
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.android.dcc.vm.domain.IdCardCertData
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.dcc.BuildConfig
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.*
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList

object CertOperations {

    const val ERROR_SUBMIT_ID_NOT_MATCH = "提交认证信息与已认证身份证信息不一致"

    private const val DIGEST = "SHA256"

    private lateinit var certPrefs: CertPrefs

    fun init(context: Context) {
        certPrefs = CertPrefs(context.getSharedPreferences("dcc_certification_status", Context.MODE_PRIVATE))
    }

    fun confirmCertFee(context: () -> FragmentManager, business: String, proceedAction: () -> Unit) {
        App.get().chainGateway.getExpectedFee(business)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val fee = it.toLong()
                    CertFeeConfirmDialog.create(fee, proceedAction).show(context(), "confirm_cert_fee")
//                    if (fee > 0){
//                        CertFeeConfirmDialog.create(proceedAction).show(context(),"confirm_cert_fee")
//                    }else{
//                        proceedAction()
//                    }
                }, {
                    //todo
                    stackTrace(it)
                })
    }

    fun submitIdCert(passport: Passport, idCardData: IdCardCertData): Single<CertOrder> {
        require(passport.authKey != null)
        require(idCardData.essentialData.name.isNotBlank())
        require(idCardData.essentialData.id.isNotBlank())
        require(idCardData.essentialData.expired > 0)
        require(idCardData.photo!!.isNotEmpty())
        require(idCardData.idFront.isNotEmpty())
        require(idCardData.idBack.isNotEmpty())

        val credentials = passport.credential

        val name = idCardData.essentialData.name
        val id = idCardData.essentialData.id
        val expired = idCardData.essentialData.expired
        val photo: ByteArray = idCardData.photo
        val front = idCardData.idFront
        val back = idCardData.idBack

        val api = App.get().chainGateway
        val certApi = App.get().certApi
        val business = ChainGateway.BUSINESS_ID
        val authKey = passport.authKey!!
        val address = passport.address
        val nonce = privateChainNonce(address)

        return api.getCertData(address, business)
                .compose(Result.checked())
                .map {
                    val digest1 = digestIdName(name, id)
                    val content = it.content
                    if (content == null || content.digest1.isEmpty()) {
                        //ok
                    } else {
                        val de = Base64.decode(content.digest1, Base64.DEFAULT)
                        if (Arrays.equals(de, digest1)) {
                            //ok
                        } else {
                            throw IllegalStateException(ERROR_SUBMIT_ID_NOT_MATCH)
                        }
                    }
                    credentials
                }
                .observeOn(Schedulers.computation())
                .map {

                    val digest1 = digestIdName(name, id)
                    val digest2 = MessageDigest.getInstance(DIGEST).run {
                        update(digest1)
                        update(MessageDigest.getInstance(DIGEST).digest(front))
                        update(MessageDigest.getInstance(DIGEST).digest(back))
                        digest(MessageDigest.getInstance(DIGEST).digest(photo))
                    }
                    EthsFunctions.apply(digest1, digest2, BigInteger.valueOf(expired))
                }
                .observeOn(Schedulers.io())
                .flatMap { applyCall ->
                    Single.zip(
                            api.getCertContractAddress(business).compose(Result.checked()),
                            api.getTicket().compose(Result.checked()),
                            pair()
                    ).flatMap { (contractAddress, ticket) ->
                        val tx = applyCall.txSigned(credentials, contractAddress, nonce)
                        api.certApply(ticket.ticket, tx, null, business)
                                .compose(Result.checked())
                    }
                }
                .certOrderByTx(api, business)
                .flatMap {
                    certApi
                            .idVerify(
                                    address = address,
                                    orderId = it.orderId,
                                    realName = name,
                                    certNo = id,
                                    personalPhoto = CertApi.uploadFilePart(photo, "user.jpg", "image/jpeg", "personalPhoto"),
                                    frontPhoto = CertApi.uploadFilePart(front, "front.jpg", "image/jpeg", "frontPhoto"),
                                    backPhoto = CertApi.uploadFilePart(back, "back.jpg", "image/jpeg", "backPhoto"),
                                    signature = ParamSignatureUtil.sign(
                                            authKey.getPrivateKey(), mapOf<String, String>(
                                            "address" to address,
                                            "orderId" to it.orderId.toString(),
                                            "realName" to name,
                                            "certNo" to id,
                                            "version" to BuildConfig.VERSION_NAME
                                    )
                                    ),
                                    version = BuildConfig.VERSION_NAME
                            )
                            .compose(Result.checked())
                }
    }

    fun submitBankCardCert(
            passport: Passport,
            bankCardInfo: BankCardInfo,
            accountName: String,
            id: String,
            onOrderCreated: (Long) -> Unit
    ): Single<String> {
        require(passport.authKey != null)
        require(bankCardInfo.bankCardNo.isNotBlank())

        val api = App.get().chainGateway
        val certApi = App.get().certApi
        val business = ChainGateway.BUSINESS_BANK_CARD

        val credentials = passport.credential
        val authKey = passport.authKey!!
        val nonce = privateChainNonce(credentials.address)
        return Single.just(bankCardInfo)
                .observeOn(Schedulers.computation())
                .map {
                    val data = it.bankCardNo.toByteArray(Charsets.UTF_8) + it.phoneNo.toByteArray(Charsets.UTF_8)
                    val digest1 = MessageDigest.getInstance(DIGEST).digest(data)
                    val digest2 = byteArrayOf()
                    EthsFunctions.apply(digest1, digest2, BigInteger.ZERO)
                }
                .observeOn(Schedulers.io())
                .flatMap { applyCall ->
                    Single.zip(
                            api.getCertContractAddress(business).compose(Result.checked()),
                            api.getTicket().compose(Result.checked()),
                            pair()
                    ).flatMap { (contractAddress, ticket) ->
                        val tx = applyCall.txSigned(credentials, contractAddress, nonce)
                        api.certApply(ticket.ticket, tx, null, business)
                                .compose(Result.checked())
                    }
                }
                .certOrderByTx(api, business)
                .doOnSuccess {
                    onOrderCreated(it.orderId)
                }
                .flatMap {
                    verifyBankCardCert(passport, bankCardInfo, accountName, id, it.orderId)
                }
    }

    @JvmStatic
    fun digestIdName(name: String, id: String): ByteArray {
        val data = name.toByteArray(Charsets.UTF_8) + id.toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance(DIGEST).digest(data)
    }

    fun verifyBankCardCert(
            passport: Passport,
            bankCardInfo: BankCardInfo,
            accountName: String,
            id: String,
            orderId: Long
    ): Single<String> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return App.get().certApi
                .bankCardVerify(
                        address = address,
                        orderId = orderId,
                        bankCode = bankCardInfo.bankCode,
                        bankAccountNo = bankCardInfo.bankCardNo,
                        accountName = accountName,
                        certNo = id,
                        phoneNo = bankCardInfo.phoneNo,
                        signature = ParamSignatureUtil.sign(
                                privateKey, mapOf<String, String>(
                                "address" to address,
                                "orderId" to orderId.toString(),
                                "bankCode" to bankCardInfo.bankCode,
                                "bankAccountNo" to bankCardInfo.bankCardNo,
                                "accountName" to accountName,
                                "certNo" to id,
                                "phoneNo" to bankCardInfo.phoneNo
                        )
                        )
                )
                .compose(Result.checked())
    }

    fun advanceBankCardCert(passport: Passport, orderId: Long, ticket: String, code: String): Single<CertOrder> {
        require(passport.authKey != null)
        val authKey = passport.authKey
        requireNotNull(authKey)
        authKey!!
        return App.get().certApi.bankCardAdvance(
                address = passport.address,
                orderId = orderId,
                ticket = ticket,
                validCode = code,
                signature = ParamSignatureUtil.sign(
                        authKey.getPrivateKey(), mapOf(
                        "address" to passport.address,
                        "orderId" to orderId.toString(),
                        "ticket" to ticket,
                        "validCode" to code
                )
                )
        ).compose(Result.checked())
    }

    private val cmLogApply = EthsFunctions.apply(byteArrayOf(), byteArrayOf(), BigInteger.ZERO)

    fun obtainNewCmLogOrderId(passport: Passport): Single<CertOrder> {
        val business = ChainGateway.BUSINESS_COMMUNICATION_LOG
        val api = App.get().chainGateway
        val nonce = privateChainNonce(passport.address)
        return Single.zip(
                api.getCertContractAddress(business).compose(Result.checked()),
                api.getTicket().compose(Result.checked()),
                pair()
        ).flatMap { (contractAddress, ticket) ->
            val tx = cmLogApply.txSigned(passport.credential, contractAddress, nonce)
            api.certApply(ticket.ticket, tx, null, business)
                    .compose(Result.checked())
        }.certOrderByTx(api, business)
    }

    fun requestCommunicationLog(
            passport: Passport,
            orderId: Long,
            name: String,
            id: String,
            phoneNo: String,
            password: String
    ): Single<CertProcess> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return App.get().certApi.requestCommunicationLogData(
                address = address,
                orderId = orderId,
                userName = name,
                certNo = id,
                phoneNo = phoneNo,
                password = password,
                signature = ParamSignatureUtil.sign(
                        privateKey, mapOf(
                        "address" to address,
                        "orderId" to orderId.toString(),
                        "userName" to name,
                        "certNo" to id,
                        "phoneNo" to phoneNo,
                        "password" to password
                )
                )
        ).compose(Result.checked())
    }

    fun advanceCommunicationLog(
            passport: Passport,
            orderId: Long,
            name: String,
            id: String,
            phoneNo: String,
            password: String,
            process: CertProcess,
            captcha: String? = null,
            queryPwd: String? = null
    ): Single<CertProcess> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return App.get().certApi.requestCommunicationLogDataAdvance(
                address = address,
                orderId = orderId,
                userName = name,
                certNo = id,
                phoneNo = phoneNo,
                password = password,
                token = process.token,
                processCode = process.processCode,
                website = process.website,
                captcha = captcha,
                queryPwd = queryPwd,
                signature = ParamSignatureUtil.sign(
                        privateKey, mapOf(
                        "address" to address,
                        "orderId" to orderId.toString(),
                        "userName" to name,
                        "certNo" to id,
                        "phoneNo" to phoneNo,
                        "password" to password,
                        "token" to process.token,
                        "processCode" to process.processCode,
                        "website" to process.website,
                        "captcha" to captcha,
                        "queryPwd" to queryPwd
                )
                )
        ).compose(Result.checked())

    }

    fun getCommunicationLogReport(passport: Passport): Single<CmLogReportData> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        val orderId = certPrefs.certCmLogOrderId.get()
        val (name, id) = getCertId()!!
        val phoneNo = certPrefs.certCmLogPhoneNo.get()!!
        return App.get().certApi.getCommunicationLogReport(
                address = address,
                orderId = orderId,
                userName = name,
                certNo = id,
                phoneNo = phoneNo,
                signature = ParamSignatureUtil.sign(
                        privateKey, mapOf(
                        "address" to address,
                        "orderId" to orderId.toString(),
                        "userName" to name,
                        "certNo" to id,
                        "phoneNo" to phoneNo
                )
                )
        ).compose(Result.checked())
    }

    fun Single<String>.certOrderByTx(api: ChainGateway, business: String): Single<CertOrder> {
        return this.flatMap { txHash ->
            api.hasReceipt(txHash)
                    .compose(Result.checked())
                    .map {
                        if (!it) {
                            throw DccChainServiceException("no receipt yet")
                        }
                        txHash
                    }
                    .retryWhen(RetryWithDelay.createSimple(4, 5000L))
        }
                .flatMap {
                    api.getOrdersByTx(it, business)
                            .compose(Result.checked())
                            .map {
                                it.first()
                            }
                }
    }

    fun getCompletedCerts(): List<String> {
        return ArrayList<String>().apply {
            if (isIdCertPassed()) {
                add(ChainGateway.BUSINESS_ID)
            }
            if (isBankCertPassed()) {
                add(ChainGateway.BUSINESS_BANK_CARD)
            }
            if (getCmLogUserStatus() == UserCertStatus.DONE) {
                add(ChainGateway.BUSINESS_COMMUNICATION_LOG)
            }
        }.toList()
    }

    fun getCertIdData(): IdCardEssentialData? {
        if (!isIdCertPassed()) {
            return null
        }
        val dataStr = certPrefs.certIdData.get()
        return dataStr?.run {
            gson.fromJson(this, IdCardEssentialData::class.java)
        }
    }

    /**
     * @return idFront,idBack,photo
     */
    @WorkerThread
    fun getCertIdPics(): Triple<ByteArray, ByteArray, ByteArray>? {
        val orderId = certPrefs.certIdOrderId.get()
        if (orderId == -1L) {
            return null
        }
        val photo = File(App.get().filesDir, certIdPhotoFileName(orderId, "photo")).readBytes()
        val idFront = File(App.get().filesDir, certIdPhotoFileName(orderId, "idFront")).readBytes()
        val idBack = File(App.get().filesDir, certIdPhotoFileName(orderId, "idBack")).readBytes()
        return Triple(idFront, idBack, photo)
    }

    fun getCertId(): Pair<String, String>? {
        if (!isIdCertPassed()) {
            return null
        }
        val name = certPrefs.certRealName.get()
        val id = certPrefs.certRealId.get()
        if (name != null && id != null) {
            return name to id
        }
        return null
    }

    fun isIdCertPassed(): Boolean {
        if (certPrefs.certIdOrderId.get() == INVALID_CERT_ORDER_ID) {
            return false
        }
        val status = certPrefs.certIdStatus.get()
        if (status == null || !CertStatus.valueOf(status).isPassed()) {
            return false
        }
        return true
    }

    fun getCertBankCardData(): BankCardInfo? {
        if (!isBankCertPassed()) {
            return null
        }
        val dataStr = certPrefs.certBankCardData.get()
        return dataStr?.run {
            gson.fromJson(this, BankCardInfo::class.java)
        }
    }

    fun getBankCardCertExpired(): Long {
        return certPrefs.certBankExpired.get()
    }

    fun isBankCertPassed(): Boolean {
        if (certPrefs.certBankOrderId.get() == INVALID_CERT_ORDER_ID) {
            return false
        }
        val status = certPrefs.certBankStatus.get()
        if (status == null || !CertStatus.valueOf(status).isPassed()) {
            return false
        }
        return true
    }

    fun getCmLogUserStatus(): UserCertStatus {
        return if (certPrefs.certCmLogOrderId.get() == INVALID_CERT_ORDER_ID) {
            UserCertStatus.NONE
        } else {
            val state = certPrefs.certCmLogState.get()
            if (state == null) {
                UserCertStatus.NONE
            } else {
                UserCertStatus.valueOf(state)
            }
        }
    }

    fun getCertStatus(certificationType: CertificationType): UserCertStatus {
        return when (certificationType) {
            CertificationType.ID -> {
                if (isIdCertPassed()) {
                    UserCertStatus.DONE
                } else {
                    UserCertStatus.NONE
                }
            }
            CertificationType.BANK -> {
                if (isBankCertPassed()) {
                    UserCertStatus.DONE
                } else {
                    UserCertStatus.NONE
                }
            }
            CertificationType.MOBILE -> {
                getCmLogUserStatus()
            }
            else -> UserCertStatus.NONE
        }
    }

    fun saveIdCertData(order: CertOrder, idData: IdCardCertData) {
        certPrefs.certIdOrderId.set(order.orderId)
        certPrefs.certIdStatus.set(order.status.name)
        certPrefs.certIdSimilarity.set(order.similarity!!)
        certPrefs.certRealName.set(idData.essentialData.name)
        certPrefs.certRealId.set(idData.essentialData.id)
        certPrefs.certIdData.set(gson.toJson(idData.essentialData))
        Schedulers.io().scheduleDirect {
            val orderId = order.orderId
            File(App.get().filesDir, certIdPhotoFileName(orderId, "photo")).apply {
                ensureNewFile()
                writeBytes(idData.photo!!)
            }
            File(App.get().filesDir, certIdPhotoFileName(orderId, "idFront")).apply {
                ensureNewFile()
                writeBytes(idData.idFront)
            }
            File(App.get().filesDir, certIdPhotoFileName(orderId, "idBack")).apply {
                ensureNewFile()
                writeBytes(idData.idBack)
            }
        }
    }

    private fun certIdPhotoFileName(orderId: Long, file: String): String {
        return when (file) {
            "photo" -> "cert${File.separator}id${File.separator}$orderId.jpg"
            else -> "cert${File.separator}id${File.separator}${orderId}_$file.jpg"
        }
    }


    fun clearAllCertData() {
        certPrefs.clearAll()
    }

    fun saveBankCertData(certOrder: CertOrder, bankCardInfo: BankCardInfo) {
        certPrefs.certBankOrderId.set(certOrder.orderId)
        certPrefs.certBankStatus.set(certOrder.status.name)
        certPrefs.certBankExpired.set(certOrder.content.expired)
        certPrefs.certBankCardData.set(gson.toJson(bankCardInfo))
    }

    fun getCmLogPhoneNo(): String? {
        return certPrefs.certCmLogPhoneNo.get()
    }

    fun getCmCertOrderId(): Long {
        return certPrefs.certCmLogOrderId.get()
    }

    fun getCmLogData(orderId: Long): Single<ByteArray> {
        return Single.just(certCmLogReportFileName(orderId))
                .observeOn(Schedulers.io())
                .map {
                    File(App.get().filesDir, it).readBytes()
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun onCmLogRequestSuccess(orderId: Long, phoneNo: String, password: String) {
        certPrefs.certCmLogOrderId.set(orderId)
        certPrefs.certCmLogState.set(UserCertStatus.INCOMPLETE.name)
        certPrefs.certCmLogPhoneNo.set(phoneNo)
        certPrefs.certCmLogPassword.set(password)
    }

    fun onCmLogFail() {
        certPrefs.run {
            certCmLogState.clear()
            certCmLogOrderId.clear()
            certCmLogPhoneNo.clear()
            certCmLogPassword.clear()
        }
    }

    fun onCmLogSuccessGot(reportData: String) {
        certPrefs.certCmLogState.set(UserCertStatus.DONE.name)
        val orderId = certPrefs.certCmLogOrderId.get()
        Schedulers.io().scheduleDirect {
            File(App.get().filesDir, certCmLogReportFileName(orderId)).apply {
                ensureNewFile()
                writeBytes(Base64.decode(reportData, Base64.DEFAULT))
            }
        }
    }

    private fun certCmLogReportFileName(orderId: Long) =
            "cert${File.separator}cmlog${File.separator}$orderId.dat"

    private val gson = Gson()
    private const val INVALID_CERT_ORDER_ID = -1L

    private val reportListType = object : TypeToken<List<LoanReport>>() {}.type

    var reportData: Pair<List<LoanReport>, Long>?
        get() = certPrefs.run {
            val report = reportData.get()?.run { gson.fromJson<List<LoanReport>>(this, reportListType) }
            if (report == null) null else report to reportUpdateTime.get()
        }
        set(value) {
            if (value != null) {
                certPrefs.reportData.set(gson.toJson(value.first))
                certPrefs.reportUpdateTime.set(value.second)
            } else {
                certPrefs.reportData.clear()
                certPrefs.reportUpdateTime.clear()
            }
        }

    private class CertPrefs(sp: SharedPreferences) : Prefs(sp) {
        // id cert order id key changed to invalidate previous cert record ; modified@2018-07-06

        //report
        val reportData = StringPref("loanReportDataList_v2")
        val reportUpdateTime = LongPref("loanReportUpdateTime_v2", -1L)

        //id
        val certIdOrderId = LongPref("certIdOrderId_v2", INVALID_CERT_ORDER_ID)
        val certIdStatus = StringPref("certIdStatus")
        val certIdSimilarity = StringPref("certIdSimilarity")
        val certRealName = StringPref("certRealName")
        val certRealId = StringPref("certRealId")
        val certIdData = StringPref("certIdData")

        //bank card
        val certBankOrderId = LongPref("certBankOrderId_v2", INVALID_CERT_ORDER_ID)
        val certBankStatus = StringPref("certBankStatus")
        val certBankExpired = LongPref("certBankExpired", -1L)
        val certBankCardData = StringPref("certBankCardData")

        //communication log
        val certCmLogOrderId = LongPref("certCmLogOrderId_v2", INVALID_CERT_ORDER_ID)
        val certCmLogState = StringPref("certCmLogState")
        val certCmLogPhoneNo = StringPref("certCmLogPhoneNo")
        val certCmLogPassword = StringPref("certCmLogPassword")
    }
}
