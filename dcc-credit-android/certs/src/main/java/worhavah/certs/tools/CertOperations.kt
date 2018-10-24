package worhavah.certs.tools

import EventMsg
import RxBus
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentManager
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.Prefs
import io.wexchain.android.common.UrlManage
import io.wexchain.android.common.kotlin.weak
import io.wexchain.android.common.stackTrace
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.android.idverify.IdVerifyHelper
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.CertApi2
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.CertOrder
import io.wexchain.dccchainservice.domain.CertProcess
import io.wexchain.dccchainservice.domain.CertStatus
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.DateUtil.getCurrentDate2
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import worhavah.certs.beans.BeanValidHomeResult
import worhavah.certs.beans.BeanValidMailResult
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.views.CertsCertFeeConfirmDialog
import worhavah.regloginlib.Net.ChainGateway
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Net.beans.CertOrderUpdatedEvent
import worhavah.regloginlib.Passport
import worhavah.regloginlib.tools.*
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


object CertOperations {
    lateinit var certApi: CertApi
    lateinit var certApi2: CertApi2
    lateinit var insCertApi: insCertApi
    lateinit var marketingApi: MarketingApi
    lateinit var idVerifyHelper: IdVerifyHelper
    var context: Context? by weak()
    lateinit var tnCertApi: tnCertApi

    private const val DIGEST = "SHA256"

    lateinit var certPrefs: CertPrefs

    fun init(context: Context) {
        certPrefs = CertPrefs(context.getSharedPreferences("dcc_certification_status", Context.MODE_PRIVATE))
        certApi = Networkutils.networking.createApi(CertApi::class.java, UrlManage.CHAIN_FUNC_URL)
        certApi2 = Networkutils.networking.createApi(CertApi2::class.java, UrlManage.CHAIN_FUNC_URL2)
        insCertApi = Networkutils.networking.createApi(worhavah.certs.tools.insCertApi::class.java, UrlManage.BaseRnsUrl)
        marketingApi = Networkutils.networking.createApi(MarketingApi::class.java, UrlManage.DCC_MARKETING_API_URL)
        tnCertApi = Networkutils.networking.createApi(worhavah.certs.tools.tnCertApi::class.java, UrlManage.TN_URL)
        this.context = context
        RxBus.getInstance().toObservable().map {
            it as EventMsg
        }.subscribe {
            if (it != null && it.msg.equals("clearAllCertData")) {
                clearAllCertData()
            }
        }
        idVerifyHelper = IdVerifyHelper(context)
        setFreeData()// 初始化数据 打开限制
    }

    private fun setFreeData() {
        certPrefs.makeStringprf("Cert" + ChainGateway.TN_COMMUNICATION_LOG + "txhashcode").set("")
    }


    fun confirmCertFee(context: () -> FragmentManager, business: String, proceedAction: () -> Unit) {
        Networkutils.chainGateway.getExpectedFee(business)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val fee = BigInteger(it)// it.toLong()
                    CertsCertFeeConfirmDialog.create(fee, proceedAction).show(context(), "confirm_cert_fee")
//                    if (fee > 0){
//                        CertsCertFeeConfirmDialog.create(proceedAction).show(context(),"confirm_cert_fee")
//                    }else{
//                        proceedAction()
//                    }
                }, {
                    //todo
                    stackTrace(it)
                })
    }

    private val cmLogApply = EthsFunctions.apply(byteArrayOf(), byteArrayOf(), BigInteger.ZERO)

    fun obtainNewCmLogOrderId(passport: Passport): Single<CertOrder> {
        val business = ChainGateway.TN_COMMUNICATION_LOG
        val api = Networkutils.chainGateway
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

    var TNnonce = ""
    fun obtainNewCmLogUpdateOrderId(passport: Passport, pn: String): Single<CertOrderUpdatedEvent> {
        val business = ChainGateway.TN_COMMUNICATION_LOG
        val api = Networkutils.chainGateway
        val nonce = privateChainNonce(passport.address)
        TNnonce = nonce.toString()
        return Single.zip(
                api.getCertContractAddress(business).compose(Result.checked()),
                api.getTicket().compose(Result.checked()),
                pair()
        ).flatMap { (contractAddress, ticket) ->
            val data = pn.toByteArray(Charsets.UTF_8) + nonce.toString().toByteArray(Charsets.UTF_8)
            val digest1 = MessageDigest.getInstance(DIGEST).digest(data)
            val ccApply = EthsFunctions.apply(byteArrayOf(), digest1, BigInteger.ZERO)
            val tx = ccApply.txSigned(passport.credential, contractAddress, nonce)
            api.certApply(ticket.ticket, tx, null, business)
                    .compose(Result.checked())
        }.certUpdateOrderByTx(api, business)
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

    fun Single<String>.certUpdateOrderByTx(api: ChainGateway, business: String): Single<CertOrderUpdatedEvent> {
        return this.flatMap { rtxHash ->
            var txHash = rtxHash
            api.getReceiptResult(txHash)
                    .compose(Result.checked())
                    //  .doOnError {  certPrefs.makeStringprf("Cert"+business+"txhashcode").set("") }
                    .map {
                        if (!it.hasReceipt) {
                            throw DccChainServiceException("订单已超时,同牛运营商认证失败")
                        }
                        it
                    }
                    .retryWhen(RetryWithDelay.createSimple(10, 3000L))
                    .map {
                        if (!it.approximatelySuccess) {
                            throw DccChainServiceException()
                        }
                        txHash
                    }
        }
                .flatMap {
                    certPrefs.makeStringprf("Cert" + business + "txhashcode").set("")
                    api.getOrderUpdatedEventsByTx(it, business)
                            .compose(Result.checked())
                            .map {
                                it.first()
                            }
                }
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

    fun getTnLogPhoneNo(): String? {
        return certPrefs.certTNLogPhoneNo.get()
    }

    fun isIdCertPassed(): Boolean {
        /* if (certPrefs.certIdOrderId.get() == INVALID_CERT_ORDER_ID) {
             return false
         }*/
        val status = certPrefs.certIdStatus.get()
        if (status == null || !CertStatus.valueOf(status).isPassed()) {
            return false
        }
        return true
    }

    fun savePNCertData(order: BeanValidResult) {
        certPrefs.certPhoneNum.set(order.phoneNum)
        certPrefs.certPhoneData.set(order.verifyDate)
    }

    fun getcertPhoneNum(): String? {
        return certPrefs.certPhoneNum.get()
    }

    fun getcertPhoneData(): String? {
        return certPrefs.certPhoneData.get()
    }

    fun saveEmCertData(order: BeanValidMailResult) {
        certPrefs.certMailNum.set(order.mailAddress)
        certPrefs.certMailData.set(order.verifyDate)
    }

    fun getcertEmCNum(): String? {
        return certPrefs.certMailNum.get()
    }

    fun getcertEmCData(): String? {
        return certPrefs.certMailData.get()
    }

    fun saveHomeAddressData(order: BeanValidHomeResult, line1: String, line2: String) {
        certPrefs.certHANum.set(order.homeAddress)
        certPrefs.certHAData.set(order.verifyDate)
        certPrefs.certHALine1.set(line1)
        certPrefs.certHALine2.set(line2)

    }

    fun getcertHANum(): String? {
        return certPrefs.certHANum.get()
    }

    fun getcertHAData(): String? {
        return certPrefs.certHAData.get()
    }

    private fun certIdPhotoFileName(order: CertOrder) =
            "cert${File.separator}id${File.separator}${order.orderId}.jpg"

    fun clearAllCertData() {
        certPrefs.clearAll()
        val eventMsg = EventMsg()
        eventMsg.msg = "finishApp"
        RxBus.getInstance().post(eventMsg)
    }

    fun clearTNCertCache() {
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertaddress.set("")
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertID.set(-1L)
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertuserName.set("")
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertcertNo.set("")
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.set("")
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertpassword.set("")
        worhavah.certs.tools.CertOperations.certPrefs.ertTNcertsignature.set("")
    }

    fun getTNCertOrderId(): Long {
        return certPrefs.certTNLogOrderId.get()
    }

    private val gson = Gson()
    private const val INVALID_CERT_ORDER_ID = -1L


    class CertPrefs(sp: SharedPreferences) : Prefs(sp) {
        //report
        val reportData = StringPref("loanReportDataList")
        val reportUpdateTime = LongPref("loanReportUpdateTime", -1L)

        //id
        val certIdOrderId = LongPref("certIdOrderId", INVALID_CERT_ORDER_ID)
        val certIdStatus = StringPref("certIdStatus")
        val certRealName = StringPref("certRealName")
        val certRealId = StringPref("certRealId")
        val certIdData = StringPref("certIdData")
        val certIdTime = StringPref("certIdTime")
        val certIdSimilarity = StringPref("certIdSimilarity")
        //bank card
        val certBankOrderId = LongPref("certBankOrderId", INVALID_CERT_ORDER_ID)
        val certBankStatus = StringPref("certBankStatus")
        val certBankExpired = LongPref("certBankExpired", -1L)
        val certBankCardData = StringPref("certBankCardData")

        //communication log
        val certCmLogOrderId = LongPref("certCmLogOrderId", INVALID_CERT_ORDER_ID)
        val certCmLogState = StringPref("certCmLogState")
        val certCmLogPhoneNo = StringPref("certCmLogPhoneNo")
        val certCmLogPassword = StringPref("certCmLogPassword")
        val certCmLogExpired = LongPref("certCmLogExpired", -1L)
        //TN log
        val certTNLogOrderId = LongPref("certTNLogOrderId", INVALID_CERT_ORDER_ID)
        val certTNLogState = StringPref("certTNLogState")
        val certTNLogPhoneNo = StringPref("certTNLogPhoneNo")
        val certTNLogPassword = StringPref("certTNLogPassword")
        val certTNLogData = StringPref("certTNLogData")
        val certTNLogExpired = LongPref("certCmLogExpired", -1L)

        val certTNcertID = LongPref("certTNcertID", -1L)//认证id
        val certTNcertaddress = StringPref("certTNcertaddress")//认证id
        val certTNcertuserName = StringPref("certTNcertuserName")//认证id
        val certTNcertcertNo = StringPref("certTNcertcertNo")//认证id
        val certTNcertphoneNo = StringPref("certTNcertphoneNo")//认证id
        val certTNcertpassword = StringPref("certTNcertpassword")//认证id
        val certTNcertnonce = StringPref("certTNcertnonce")//认证id
        val ertTNcertsignature = StringPref("ertTNcertsignature")//认证id


        //手机邮箱
        val certPhoneNum = StringPref("certPhoneNum")
        val certPhoneData = StringPref("certPhoneData")
        val certMailNum = StringPref("certMailNum")
        val certMailData = StringPref("certMailData")
        val certHANum = StringPref("certHANum")
        val certHAData = StringPref("certHAData")
        val certHALine1 = StringPref("certHALine1")
        val certHALine2 = StringPref("certHALine2")

        val certLasttime = StringPref("certLasttime")


        fun makeStringprf(ss: String): StringPref {
            return StringPref(ss)
        }
    }

    fun getTNLogCertExpired(): Long {
        return certPrefs.certTNLogExpired.get()
    }

    fun getLocalTnDigest(): Pair<ByteArray, ByteArray> {
        val digest1 = MessageDigest.getInstance(DIGEST).digest(CertOperations.certPrefs.certTNLogData.get()!!.toByteArray())
        return digest1 to byteArrayOf()
    }

    fun saveTnLogCertExpired(expired: Long) {
        certPrefs.certTNLogExpired.set(expired)
    }

    fun submitCert(
            passport: Passport,
            eMail: String,
            onOrderCreated: (Long) -> Unit,
            business: String = "EMAIL"
    ): Single<String> {
        require(passport.authKey != null)
        require(eMail.isNotBlank())
        val api = Networkutils.chainGateway
        val credentials = passport.credential
        val authKey = passport.authKey!!
        val nonce = privateChainNonce(credentials.address)
        return Single.just(eMail)
                .observeOn(Schedulers.computation())
                .map {
                    val data = it.toByteArray(Charsets.UTF_8)
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
                        // insCertApi.certApply(ticket.ticket, tx, null, business)
                        api.certApply(ticket.ticket, tx, null, business)
                                .compose(Result.checked())
                    }
                }
                .certOrderByTx(api, business)
                .doOnSuccess {
                    onOrderCreated(it.orderId)
                }
                .flatMap {
                    Single.just(it.toString())
                    // verifyBankCardCert(passport, bankCardInfo, accountName, id, it.orderId)
                }
    }

    fun homeAddressCert(
            passport: Passport,
            eMail: String,
            onOrderCreated: (Long) -> Unit,
            business: String = "EMAIL"
    ): Single<String> {
        require(passport.authKey != null)
        require(eMail.isNotBlank())
        val api = Networkutils.chainGateway
        val credentials = passport.credential
        val authKey = passport.authKey!!
        val nonce = privateChainNonce(credentials.address)
        return Single.just(eMail)
                .observeOn(Schedulers.computation())
                .map {
                    val data = it.toByteArray(Charsets.UTF_8)
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
                        // insCertApi.certApply(ticket.ticket, tx, null, business)
                        api.certApply(ticket.ticket, tx, null, business)
                                .compose(Result.checked())
                    }
                }
                .certOrderByTx(api, business)
                .doOnSuccess {
                    onOrderCreated(it.orderId)
                }
                .flatMap {
                    Single.just(it.toString())
                    // verifyBankCardCert(passport, bankCardInfo, accountName, id, it.orderId)
                }
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
        return certApi2.requestCommunicationLogDataAdvance(
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

    fun onCmLogRequestSuccess(orderId: Long, phoneNo: String, password: String) {
        certPrefs.certCmLogOrderId.set(orderId)
        certPrefs.certCmLogState.set(UserCertStatus.INCOMPLETE.name)
        certPrefs.certCmLogPhoneNo.set(phoneNo)
        certPrefs.certCmLogPassword.set(password)
    }

    fun certed() {
        certPrefs.certLasttime.set(getCurrentDate2())
    }


    fun onTNLogRequestSuccess(orderId: Long, phoneNo: String, password: String) {
        certPrefs.certTNLogOrderId.set(orderId)
        certPrefs.certTNLogState.set(UserCertStatus.INCOMPLETE.name)
        certPrefs.certTNLogPhoneNo.set(phoneNo)
        certPrefs.certTNLogPassword.set(password)
        clearTNCertCache()
    }

    fun onTNLogSuccessGot(reportData: String) {
        certPrefs.certTNLogState.set(UserCertStatus.DONE.name)
        val orderId = certPrefs.certTNLogOrderId.get()
        certPrefs.certTNLogData.set(reportData)
    }

    fun getTNLogUserStatus(): UserCertStatus {
        return if (certPrefs.certTNLogOrderId.get() == INVALID_CERT_ORDER_ID) {
            UserCertStatus.NONE
        } else {
            val state = certPrefs.certTNLogState.get()
            if (state == null) {
                UserCertStatus.NONE
            } else {
                UserCertStatus.valueOf(state)
            }
        }
    }
}
