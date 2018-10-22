package worhavah.certs.tools

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentManager
import android.util.Base64
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.kotlin.weak
import worhavah.regloginlib.tools.RetryWithDelay
import worhavah.regloginlib.tools.pair
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.android.idverify.IdVerifyHelper
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.DccChainServiceException
import worhavah.certs.beans.BeanValidHomeResult
import worhavah.certs.beans.BeanValidMailResult
import worhavah.certs.beans.BeanValidResult
import worhavah.certs.views.CertsCertFeeConfirmDialog
import worhavah.regloginlib.Net.ChainGateway
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList
import EventMsg
import RxBus
import android.support.annotation.WorkerThread
import android.util.Log
import io.wexchain.android.common.*
import io.wexchain.dccchainservice.CertApi2
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.*
import io.wexchain.dccchainservice.util.DateUtil.*
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import org.web3j.crypto.Credentials
import worhavah.certs.BuildConfig
import worhavah.certs.CertListner
import worhavah.certs.beans.BankCardInfo
import worhavah.regloginlib.AuthKey
import worhavah.regloginlib.EthsHelper
import worhavah.regloginlib.Net.beans.CertOrderUpdatedEvent
import worhavah.regloginlib.PassportRepository
import worhavah.regloginlib.tools.*
import java.text.SimpleDateFormat


object CertOperations {
    lateinit var certApi: CertApi
    lateinit var certApi2: CertApi2
    lateinit var insCertApi: insCertApi
    lateinit var marketingApi: MarketingApi
    lateinit var idVerifyHelper: IdVerifyHelper
    var context: Context? by weak()
    lateinit var tnCertApi: tnCertApi

    const val ERROR_SUBMIT_ID_NOT_MATCH = "提交认证信息与已认证身份证信息不一致"

    private const val DIGEST = "SHA256"

    public lateinit var certPrefs: CertPrefs

    fun init(context: Context) {
        certPrefs = CertPrefs(context.getSharedPreferences("dcc_certification_status", Context.MODE_PRIVATE))
        certApi = Networkutils.networking.createApi(CertApi::class.java, UrlManage.CHAIN_FUNC_URL)
        certApi2 = Networkutils.networking.createApi(CertApi2::class.java, UrlManage.CHAIN_FUNC_URL2)
        insCertApi = Networkutils.networking.createApi(worhavah.certs.tools.insCertApi::class.java, UrlManage.BaseRnsUrl)
        marketingApi = Networkutils.networking.createApi(MarketingApi::class.java, UrlManage.DCC_MARKETING_API_URL)
        tnCertApi= Networkutils.networking.createApi(worhavah.certs.tools.tnCertApi::class.java, UrlManage.TN_URL)
        this.context = context
        RxBus.getInstance().toObservable().map{
                 it as EventMsg
        }.subscribe( {
                if (it != null&&it.msg.equals("clearAllCertData")) {
                    clearAllCertData()
                }
        })
        idVerifyHelper = IdVerifyHelper(context)
        setFreeData()// 初始化数据 打开限制
    }

    private fun setFreeData() {
        certPrefs.makeStringprf("Cert"+ChainGateway.TN_COMMUNICATION_LOG+"txhashcode").set("")
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

        val api = Networkutils.chainGateway
        val certApi = CertOperations.certApi
        val business = io.wexchain.dccchainservice.ChainGateway.BUSINESS_BANK_CARD

        val credentials = passport.credential
        val authKey = passport.authKey!!
        val nonce = privateChainNonce(credentials.address)
        return Single.just(bankCardInfo)
            .observeOn(Schedulers.computation())
            .map {
                val data = it.bankCardNo.toByteArray(Charsets.UTF_8) + it.phoneNo.toByteArray(Charsets.UTF_8)
                val digest1 = MessageDigest.getInstance( DIGEST).digest(data)
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
        return CertOperations.certApi2
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
    fun confirmCertFee(context: () -> FragmentManager, business: String, proceedAction: () -> Unit) {
        Networkutils.chainGateway.getExpectedFee(business)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val fee =BigInteger(it)// it.toLong()
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

    fun submitIdCert(passport: Passport, idCardData: IdCardEssentialData, photo: ByteArray): Single<CertOrder> {
        require(passport.authKey != null)
        require(idCardData.name.isNotBlank())
        require(idCardData.id.isNotBlank())
        require(idCardData.expired > 0)
        require(photo.isNotEmpty())

        val credentials = passport.credential

        val name = idCardData.name
        val id = idCardData.id
        val expired = idCardData.expired

        val api = Networkutils.chainGateway
        val certApi = certApi
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
                .flatMap { order ->
                    ScfOperations
                            .withScfTokenInCurrentPassport {
                                insCertApi
                                        .apply(token = it,
                                                // address = address,
                                                orderId = order.orderId,
                                                realName = name,
                                                certNo = id,
                                                file = CertApi.uploadFilePart(photo, "user.jpg", "image/jpeg"))
                            }

                }
    }


    @JvmStatic
    fun digestIdName(name: String, id: String): ByteArray {
        val data = name.toByteArray(Charsets.UTF_8) + id.toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance(DIGEST).digest(data)
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
    var TNnonce=""
    fun obtainNewCmLogUpdateOrderId(passport: Passport,pn:String): Single<CertOrderUpdatedEvent> {
        val business = ChainGateway.TN_COMMUNICATION_LOG
        val api = Networkutils.chainGateway
        val nonce = privateChainNonce(passport.address)
        TNnonce=nonce.toString()
        return Single.zip(
            api.getCertContractAddress(business).compose(Result.checked()),
            api.getTicket().compose(Result.checked()),
            pair()
        ).flatMap { (contractAddress, ticket) ->
            val data = pn.toByteArray(Charsets.UTF_8) + nonce.toString().toByteArray(Charsets.UTF_8)
            val digest1 = MessageDigest.getInstance(DIGEST).digest(data)
            val ccApply=EthsFunctions.apply(byteArrayOf(), digest1, BigInteger.ZERO)
            val tx = ccApply.txSigned(passport.credential, contractAddress, nonce)
            api.certApply(ticket.ticket, tx, null, business)
                .compose(Result.checked())
        }.certUpdateOrderByTx(api, business)
    }

    fun getCompletedCerts(): List<String> {
        return ArrayList<String>().apply {
            if (isIdCertPassed()) {
                add(ChainGateway.BUSINESS_ID)
            }
            /* if(isBankCertPassed()){
                 add(ChainGateway.BUSINESS_BANK_CARD)
             }
             if(getCmLogUserStatus() == UserCertStatus.DONE){
                 add(ChainGateway.BUSINESS_COMMUNICATION_LOG)
             }*/
        }.toList()
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

    var txHashTime=0
    fun Single<String>.certUpdateOrderByTx(api: ChainGateway, business: String): Single<CertOrderUpdatedEvent> {
        return this.flatMap { rtxHash ->
            var txHash=rtxHash
            /*if(android.text.TextUtils.isEmpty(  certPrefs.makeStringprf("Cert"+business+"txhashcode").get())){
                certPrefs.makeStringprf("Cert"+business+"txhashcode").set(rtxHash)
            }else{
                txHash= certPrefs.makeStringprf("Cert"+business+"txhashcode").get()!!
            }*/
            /*api.hasReceipt(txHash)
                .compose(Result.checked())
                .doOnError {  certPrefs.makeStringprf("Cert"+business+"txhashcode").set("") }
                .map {
                    if (!it) {
                        throw DccChainServiceException("no receipt yet")
                    }
                    txHash
                }*/
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
                certPrefs.makeStringprf("Cert"+business+"txhashcode").set("")
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
    fun setCertId(id: String,name: String){
        certPrefs.certRealId.set(id)
        certPrefs.certRealName.set(name)
    }
    fun getCmLogPhoneNo(): String? {
        return certPrefs.certCmLogPhoneNo.get()
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
            CertificationType.TONGNIU -> {
                val i= getTNLogUserStatus()
                /*when (i){
                    worhavah.certs.tools . UserCertStatus.DONE -> UserCertStatus.DONE
                    worhavah.certs.tools .  UserCertStatus.NONE -> UserCertStatus.NONE
                    worhavah.certs.tools .  UserCertStatus.INCOMPLETE -> UserCertStatus.INCOMPLETE
                }*/
i
            }
            else -> UserCertStatus.NONE
        }
    }

    fun saveIdCertData(order: CertOrder, idData: IdCardEssentialData, photo: ByteArray) {
        certPrefs.certIdOrderId.set(order.orderId)
        certPrefs.certIdStatus.set(order.status.name)
        certPrefs.certRealName.set(idData.name)
        certPrefs.certRealId.set(idData.id)
        certPrefs.certIdData.set(gson.toJson(idData))
        certPrefs.certIdTime.set( SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(Date(System.currentTimeMillis())))
        Schedulers.io().scheduleDirect {
            File(context!!.filesDir, certIdPhotoFileName(order)).apply {
                ensureNewFile()
                writeBytes(photo)
            }
        }
    }
    fun savePNCertData(order: BeanValidResult) {
        certPrefs.certPhoneNum.set(order.phoneNum)
        certPrefs.certPhoneData.set(order.verifyDate)
    }
    fun getcertPhoneNum():String?{
        return certPrefs.certPhoneNum.get()
    }
    fun getcertPhoneData():String?{
        return certPrefs.certPhoneData.get()
    }
    fun saveEmCertData(order: BeanValidMailResult) {
        certPrefs.certMailNum.set(order.mailAddress)
        certPrefs.certMailData.set(order.verifyDate)
    }
    fun getcertEmCNum():String?{
        return certPrefs.certMailNum.get()
    }
    fun getcertEmCData():String?{
        return certPrefs.certMailData.get()
    }
    fun saveHomeAddressData(order: BeanValidHomeResult,line1:String,line2:String) {
        certPrefs.certHANum.set(order.homeAddress)
        certPrefs.certHAData.set(order.verifyDate)
        certPrefs.certHALine1.set(line1)
        certPrefs.certHALine2.set(line2)

    }
    fun getcertHANum():String?{
        return certPrefs.certHANum.get()
    }
    fun getcertHAData():String?{
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
    fun clearTNCertCache(){
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertaddress.set("" )
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertID.set(-1L )
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertuserName.set("" )
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertcertNo.set("" )
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.set("" )
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertpassword.set("" )
        worhavah.certs.tools.CertOperations.certPrefs.ertTNcertsignature.set("" )
    }


    fun getCmCertOrderId(): Long {
        return certPrefs.certCmLogOrderId.get()
    }
    fun getTNCertOrderId(): Long {
        return certPrefs.certTNLogOrderId.get()
    }

    private val gson = Gson()
    private const val INVALID_CERT_ORDER_ID = -1L


    public class CertPrefs(sp: SharedPreferences) : Prefs(sp) {
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

        val certTNcertID = LongPref("certTNcertID",-1L)//认证id
        val certTNcertaddress = StringPref("certTNcertaddress")//认证id
        val certTNcertuserName = StringPref("certTNcertuserName")//认证id
        val certTNcertcertNo = StringPref("certTNcertcertNo")//认证id
        val certTNcertphoneNo = StringPref("certTNcertphoneNo")//认证id
        val certTNcertpassword = StringPref("certTNcertpassword")//认证id
        val certTNcertnonce = StringPref("certTNcertnonce")//认证id
        val ertTNcertsignature= StringPref("ertTNcertsignature")//认证id







        //手机邮箱
        val certPhoneNum= StringPref("certPhoneNum")
        val certPhoneData = StringPref("certPhoneData")
        val certMailNum= StringPref("certMailNum")
        val certMailData = StringPref("certMailData")
        val certHANum= StringPref("certHANum")
        val certHAData = StringPref("certHAData")
        val certHALine1= StringPref("certHALine1")
        val certHALine2= StringPref("certHALine2")

        val certLasttime= StringPref("certLasttime")


        fun makeStringprf(ss:String):StringPref{
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

    fun String264(){
        var sssss="hskhkajhkjhkjhkh1231"
        var data=Base64.encodeToString(sssss.toByteArray(), Base64.DEFAULT)
        val digest1 = MessageDigest.getInstance(DIGEST).digest(data.toByteArray())
        val digest11 = MessageDigest.getInstance(DIGEST).digest(sssss.toByteArray())
       // Log.e("t get64digest",data)
        val dig264String= String (Base64.encode(digest1,Base64.DEFAULT) )
      //  Log.e("t dig264String",dig264String)
        val dig264String222= String (Base64.encode(digest11,Base64.DEFAULT) )
      //  Log.e("t dig264String222",dig264String222)
        val dig264String2= String(digest1)
    //    Log.e("t dig264String2",dig264String2)
        var dd=Base64.decode(sssss.toByteArray(), Base64.DEFAULT)
      //  var dd2=Base64.decode(sssss.toByteArray(), Base64.DEFAULT)
      //  Log.e("t get64digest 1",String(dd))
        val digest2 = MessageDigest.getInstance(DIGEST).digest(dd)
        val dig264String22= String (Base64.encode(digest2,Base64.DEFAULT) )
     //   Log.e("t dig264String22",dig264String22)
        val dig264String23= String(digest2)
      //  Log.e("t dig264String23",dig264String23)
    }
    fun saveTnLogCertExpired(expired: Long) {
        certPrefs.certTNLogExpired.set(expired)
    }
    fun submitCert(
        passport: Passport,
        eMail: String,
        onOrderCreated: (Long) -> Unit,
        business:String = "EMAIL"
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
        business:String = "EMAIL"
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

        val api = Networkutils.chainGateway
        val certApi = certApi
        val business = io.wexchain.dccchainservice.ChainGateway.BUSINESS_ID
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
                certApi2
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


    fun saveIdCertData(order: CertOrder, idData: IdCardCertData) {
        certPrefs.certIdOrderId.set(order.orderId)
        certPrefs.certIdStatus.set(order.status.name)
      //  certPrefs.certIdSimilarity.set(order.similarity!!)
        certPrefs.certRealName.set(idData.essentialData.name)
        certPrefs.certRealId.set(idData.essentialData.id)
        certPrefs.certIdData.set(gson.toJson(idData.essentialData))
        Schedulers.io().scheduleDirect {
            val orderId = order.orderId
            File(Networkutils.filesDir, certIdPhotoFileName(orderId, "photo")).apply {
                ensureNewFile()
                writeBytes(idData.photo!!)
            }
            File(Networkutils.filesDir, certIdPhotoFileName(orderId, "idFront")).apply {
                ensureNewFile()
                writeBytes(idData.idFront)
            }
            File(Networkutils.filesDir, certIdPhotoFileName(orderId, "idBack")).apply {
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
    private var certListner: CertListner? = null

    public fun setCertListener(certListner: CertListner){
        this.certListner = certListner
    }
    fun onCertSuc(){
        if(null!=certListner){
            certListner!!.certSuccess()
        }
    }
    fun onCertError(){
        if(null!=certListner){
            certListner!!.certOnError()
        }
    }
    public fun createNewAndEnablePassport(password: String,activity: Context): Single<Pair<Credentials, AuthKey>> {
        require(password.isNotBlank())
        return Single.just(password)
            .observeOn(Schedulers.computation())
            .map {
                EthsHelper.createNewCredential()
            }
            .map {
                val authKey = EthsHelper.createAndroidRSAKeyPair(context = activity).let {
                    AuthKey(it.second, it.first.public.encoded)
                }
                it to authKey
            }
            .observeOn(Schedulers.io())
            .flatMap {
                Networkutils.chainGateway.getTicket()
                    .compose(Result.checked())
                    .flatMap { ticket ->
                        PassportOperations.uploadPubKeyChecked(it, ticket.ticket, null,activity)
                    }
            }
            .doOnSuccess {
                Networkutils.passportRepository.saveNewPassport(it.first, password, it.second)
                /*Networkutils.passportRepository.addAuthKeyChangedRecord(
                    AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE)
                )*/
            }
            .observeOn(AndroidSchedulers.mainThread())

    }
//rss私钥  钱包私钥 钱包地址
    public fun createNewAndEnablePassportbyinput(password: String,ethPrivateKey: String,activity: Context): Single<Pair<Credentials, AuthKey>> {
        require(password.isNotBlank())
        return Single.just(password)
            .observeOn(Schedulers.computation())
            .map {
                EthsHelper.createNewCredentialbyPrivateKey(ethPrivateKey)
            }
            .map {
                val authKey = EthsHelper.createAndroidRSAKeyPair(context = activity).let {
                    AuthKey(it.second, it.first.public.encoded)
                }
                it to authKey
            }
            .observeOn(Schedulers.io())
            .flatMap {
                Networkutils.chainGateway.getTicket()
                    .compose(Result.checked())
                    .flatMap { ticket ->
                        PassportOperations.uploadPubKeyChecked(it, ticket.ticket, null,activity)
                    }
            }
            .doOnSuccess {
                Networkutils.passportRepository.saveNewPassport(it.first, password, it.second)
               /* Networkutils.passportRepository.addAuthKeyChangedRecord(
                    AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE)
                )*/
            }
            .observeOn(AndroidSchedulers.mainThread())

    }

    public fun createNewAndEnablePassportbyinput2(credentials:Credentials,activity: Context): Single<Pair<Credentials, AuthKey>> {

        return Single.just(credentials)
            .observeOn(Schedulers.computation())
            .map {
                val authKey = EthsHelper.createAndroidRSAKeyPair(context = activity).let {
                    AuthKey(it.second, it.first.public.encoded)
                }
                it to authKey
            }
            .observeOn(Schedulers.io())
            .flatMap {
                Networkutils.chainGateway.getTicket()
                    .compose(Result.checked())
                    .flatMap { ticket ->
                        PassportOperations.uploadPubKeyChecked(it, ticket.ticket, null,activity)
                    }
            }
            .doOnSuccess {
                Networkutils.passportRepository.saveNewPassR(it.first,   it.second)
                /* Networkutils.passportRepository.addAuthKeyChangedRecord(
                     AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE)
                 )*/
            }
            .observeOn(AndroidSchedulers.mainThread())

    }

    fun advanceBankCardCert(passport: Passport, orderId: Long, ticket: String, code: String): Single<CertOrder> {
        require(passport.authKey != null)
        val authKey = passport.authKey
        requireNotNull(authKey)
        authKey!!
        return  certApi2.bankCardAdvance(
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

    fun saveBankCertData(certOrder: CertOrder, bankCardInfo: BankCardInfo) {
        certPrefs.certBankOrderId.set(certOrder.orderId)
        certPrefs.certBankStatus.set(certOrder.status.name)
        certPrefs.certBankExpired.set(certOrder.content.expired)
        certPrefs.certBankCardData.set(gson.toJson(bankCardInfo))
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
        return  certApi2.requestCommunicationLogData(
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
        return  certApi2.requestCommunicationLogDataAdvance(
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

    /**
     * @return idFront,idBack,photo
     */
    @WorkerThread
    fun getCertIdPics(): Triple<ByteArray, ByteArray, ByteArray>? {
        val orderId = certPrefs.certIdOrderId.get()
        if (orderId == -1L) {
            return null
        }
        val photo = File(context!!.filesDir, certIdPhotoFileName(orderId, "photo")).readBytes()
        val idFront = File(context!!.filesDir, certIdPhotoFileName(orderId, "idFront")).readBytes()
        val idBack = File(context!!.filesDir, certIdPhotoFileName(orderId, "idBack")).readBytes()
        return Triple(idFront, idBack, photo)
    }

    fun onCmLogRequestSuccess(orderId: Long, phoneNo: String, password: String) {
        certPrefs.certCmLogOrderId.set(orderId)
        certPrefs.certCmLogState.set(UserCertStatus.INCOMPLETE.name)
        certPrefs.certCmLogPhoneNo.set(phoneNo)
        certPrefs.certCmLogPassword.set(password)
    }

    fun certed(){
        certPrefs.certLasttime.set(getCurrentDate2())
    }


    fun onTNLogRequestSuccess(orderId: Long, phoneNo: String, password: String) {
        certPrefs.certTNLogOrderId.set(orderId)
        certPrefs.certTNLogState.set(UserCertStatus.INCOMPLETE.name)
        certPrefs.certTNLogPhoneNo.set(phoneNo)
        certPrefs.certTNLogPassword.set(password)
        clearTNCertCache()
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
    private fun certCmLogReportFileName(orderId: Long) =
        "cert${File.separator}cmlog${File.separator}$orderId.dat"
    private fun certTNLogReportFileName(orderId: Long) =
        "cert${File.separator}tnlog${File.separator}$orderId.dat"
    fun getCmLogData(orderId: Long): Single<ByteArray> {
        return Single.just(certCmLogReportFileName(orderId))
            .observeOn(Schedulers.io())
            .map {
                File(context!!.filesDir, it).readBytes()
            }
            .observeOn(AndroidSchedulers.mainThread())
    }
    fun getTNLogData(orderId: Long): Single<ByteArray> {
        return Single.just(certTNLogReportFileName(orderId))
            .observeOn(Schedulers.io())
            .map {
                File(context!!.filesDir, it).readBytes()
            }
            .observeOn(AndroidSchedulers.mainThread())
    }
    fun getCommunicationLogReport(passport: Passport): Single<CmLogReportData> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        val orderId = certPrefs.certCmLogOrderId.get()
        val (name, id) = getCertId()!!
        val phoneNo = certPrefs.certCmLogPhoneNo.get()!!
        return certApi.getCommunicationLogReport(
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
    fun onCmLogFail() {
        certPrefs.run {
            certCmLogState.clear()
            certCmLogOrderId.clear()
            certCmLogPhoneNo.clear()
            certCmLogPassword.clear()
        }
    }
    fun queryCmLog(){
        if(getCmLogUserStatus()== UserCertStatus.INCOMPLETE){
            val passport = PassportRepository.getCurrentPassport()!!
            getCommunicationLogReport(passport)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    if (data.fail) {
                        // generate report fail
                        onCmLogFail()
                        queryCmLog()
                    } else {
                        val reportData = data.reportData
                        if (data.hasCompleted() && reportData != null) {
                            onCmLogSuccessGot(reportData)
                            queryCmLog()
                        }
                    }
                },{
                    it.printStackTrace()
                })
        }

    }
  /*  fun getTNLogUserStatus(): UserCertStatus {
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
    }*/
    fun onTNLogSuccessGot(reportData: String) {
        certPrefs.certTNLogState.set(UserCertStatus.DONE.name)
        val orderId = certPrefs.certTNLogOrderId.get()
      certPrefs.certTNLogData.set(reportData)
        /*Schedulers.io().scheduleDirect {
            File(context!!.filesDir, certTNLogReportFileName(orderId)).apply {
                ensureNewFile()
                writeBytes(Base64.decode(reportData, Base64.DEFAULT))
            }
        }*/
    }

    fun onCmLogSuccessGot(reportData: String) {
        certPrefs.certCmLogState.set(UserCertStatus.DONE.name)
        val orderId = certPrefs.certCmLogOrderId.get()
        Schedulers.io().scheduleDirect {
            File(context!!.filesDir, certCmLogReportFileName(orderId)).apply {
                ensureNewFile()
                writeBytes(Base64.decode(reportData, Base64.DEFAULT))
            }
        }
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