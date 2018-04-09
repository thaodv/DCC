package io.wexchain.android.dcc.chain

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentManager
import com.google.gson.Gson
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
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CertOrder
import io.wexchain.dccchainservice.domain.CertProcess
import io.wexchain.dccchainservice.domain.CertStatus
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

object CertOperations {
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
                    CertFeeConfirmDialog.create(proceedAction).show(context(), "confirm_cert_fee")
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

        val api = App.get().chainGateway
        val certApi = App.get().certApi
        val business = ChainGateway.BUSINESS_ID
        val authKey = passport.authKey!!
        val nonce = privateChainNonce(credentials.address)

        return Single.just(credentials)
                .observeOn(Schedulers.computation())
                .map {

                    val data = name.toByteArray(Charsets.UTF_8) + id.toByteArray(Charsets.UTF_8)
                    val digest1 = MessageDigest.getInstance(DIGEST).digest(data)
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
                .flatMap {
                    certApi
                            .idVerify(
                                    address = credentials.address,
                                    orderId = it.orderId,
                                    realName = name,
                                    certNo = id,
                                    file = CertApi.uploadFilePart(photo, "user.jpg", "image/jpeg"),
                                    signature = ParamSignatureUtil.sign(authKey.getPrivateKey(), mapOf<String, String>(
                                            "address" to credentials.address,
                                            "orderId" to it.orderId.toString(),
                                            "realName" to name,
                                            "certNo" to id
                                    ))
                            )
                            .compose(Result.checked())
                }
    }

    fun submitBankCardCert(passport: Passport, bankCardInfo: BankCardInfo, accountName: String, id: String, onOrderCreated: (Long) -> Unit): Single<String> {
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
                    verifyBankCardCert(passport,bankCardInfo,accountName,id,it.orderId)
                }
    }

    fun verifyBankCardCert(passport: Passport, bankCardInfo: BankCardInfo, accountName: String, id: String,orderId: Long): Single<String> {
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
                        signature = ParamSignatureUtil.sign(privateKey, mapOf<String, String>(
                                "address" to address,
                                "orderId" to orderId.toString(),
                                "bankCode" to bankCardInfo.bankCode,
                                "bankAccountNo" to bankCardInfo.bankCardNo,
                                "accountName" to accountName,
                                "certNo" to id,
                                "phoneNo" to bankCardInfo.phoneNo
                        ))
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
                signature = ParamSignatureUtil.sign(authKey.getPrivateKey(), mapOf(
                        "address" to passport.address,
                        "orderId" to orderId.toString(),
                        "ticket" to ticket,
                        "validCode" to code
                ))
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
        }.certOrderByTx(api,business)
    }

    fun requestCommunicationLog(passport: Passport,orderId: Long,name:String,id:String,phoneNo:String,password:String): Single<CertProcess> {
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return App.get().certApi.requestCommunicationLogData(
                address = address,
                orderId = orderId,
                userName = name,
                certNo = id,
                phoneNo = phoneNo,
                password = password,
                signature = ParamSignatureUtil.sign(privateKey, mapOf(
                        "address" to address,
                        "orderId" to orderId.toString(),
                        "userName" to name,
                        "certNo" to id,
                        "phoneNo" to phoneNo,
                        "password" to password
                ))
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

    fun getCertIdData(): IdCardEssentialData? {
        if (!isIdCertPassed()){
            return null
        }
        val dataStr = certPrefs.certIdData.get()
        return dataStr?.run {
            gson.fromJson(this,IdCardEssentialData::class.java)
        }
    }

    fun getCertId(): Pair<String, String>? {
        if (!isIdCertPassed()){
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

    fun getCertBankCardData():BankCardInfo?{
        if (!isBankCertPassed()){
            return null
        }
        val dataStr = certPrefs.certBankCardData.get()
        return dataStr?.run {
            gson.fromJson(this,BankCardInfo::class.java)
        }
    }

    fun getBankCardCertExpired(): Long {
        return certPrefs.certBankExpired.get()
    }

    fun isBankCertPassed():Boolean{
        if (certPrefs.certBankOrderId.get() == INVALID_CERT_ORDER_ID) {
            return false
        }
        val status = certPrefs.certBankStatus.get()
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
                if(isBankCertPassed()){
                    UserCertStatus.DONE
                }else{
                    UserCertStatus.NONE
                }
            }
            else-> UserCertStatus.NONE
//            CertificationType.PERSONAL -> TODO()
//            CertificationType.MOBILE -> TODO()
        }
    }

    fun saveIdCertData(order: CertOrder, idData: IdCardEssentialData, photo: ByteArray) {
        certPrefs.certIdOrderId.set(order.orderId)
        certPrefs.certIdStatus.set(order.status.name)
        certPrefs.certRealName.set(idData.name)
        certPrefs.certRealId.set(idData.id)
        certPrefs.certIdData.set(gson.toJson(idData))
        Schedulers.io().scheduleDirect {
            File(App.get().filesDir, certIdPhotoFileName(order)).apply {
                ensureNewFile()
                writeBytes(photo)
            }
        }
    }

    private fun certIdPhotoFileName(order: CertOrder) =
            "cert${File.separator}id${File.separator}${order.orderId}.jpg"

    fun clearAllCertData(){
        certPrefs.clearAll()
    }

    fun saveBankCertData(certOrder: CertOrder, bankCardInfo: BankCardInfo) {
        certPrefs.certBankOrderId.set(certOrder.orderId)
        certPrefs.certBankStatus.set(certOrder.status.name)
        certPrefs.certBankExpired.set(certOrder.content.expired)
        certPrefs.certBankCardData.set(gson.toJson(bankCardInfo))
    }

    private val gson = Gson()
    private const val INVALID_CERT_ORDER_ID = -1L

    private class CertPrefs(sp: SharedPreferences) : Prefs(sp) {
        //id
        val certIdOrderId = LongPref("certIdOrderId", INVALID_CERT_ORDER_ID)
        val certIdStatus = StringPref("certIdStatus")
        val certRealName = StringPref("certRealName")
        val certRealId = StringPref("certRealId")
        val certIdData = StringPref("certIdData")

        //bank card
        val certBankOrderId = LongPref("certBankOrderId", INVALID_CERT_ORDER_ID)
        val certBankStatus = StringPref("certBankStatus")
        val certBankExpired = LongPref("certBankExpired", -1L)
        val certBankCardData = StringPref("certBankCardData")
    }
}