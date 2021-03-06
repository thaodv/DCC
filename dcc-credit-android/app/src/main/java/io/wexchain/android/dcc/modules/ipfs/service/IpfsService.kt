package io.wexchain.android.dcc.modules.ipfs.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.ensureNewFile
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.modules.ipfs.EventType
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.tools.FileUtils
import io.wexchain.android.dcc.tools.formatSize
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.core.IpfsCore
import io.wexchain.ipfs.entity.BankInfo
import io.wexchain.ipfs.entity.IdInfo
import io.wexchain.ipfs.entity.PhoneInfo
import io.wexchain.ipfs.entity.TNPhoneInfo
import io.wexchain.ipfs.utils.*
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger

/**
 *Created by liuyang on 2018/8/28.
 */
class IpfsService : Service() {

    private var ID_NONCE = BigInteger("0")
    private var BANK_NONCE = BigInteger("0")
    private var CM_NONCE = BigInteger("0")
    private var TN_NONCE = BigInteger("0")

    private val rootpath by lazy {
        App.get().filesDir.absolutePath + File.separator + "cert" + File.separator + "id"
    }

    private val passport by lazy {
        App.get().passportRepository
    }

    private val mBinder = IpfsBinder(this)

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    fun createItemData(business: String): Single<String> {
        return when (business) {
            ChainGateway.BUSINESS_ID -> createIdData()
            ChainGateway.BUSINESS_BANK_CARD -> createBankData()
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> createCmData()
            ChainGateway.TN_COMMUNICATION_LOG -> createTnData()
            else -> Single.error(Throwable(""))
        }
    }

    private fun createIdData(): Single<String> {
        ID_NONCE = IpfsOperations.getNonce().blockingGet()
        return Single.create<String> {
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
                    similarity = similarity,
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

            val json = idInfo.toJson()
            val data = json.toByteArray()

            val size = getDataFromSize(data, MyCloudActivity.ID_ENTIFY_DATA, ID_NONCE)
            it.onSuccess(size)
        }
    }

    private fun createCmData(): Single<String> {
        CM_NONCE = IpfsOperations.getNonce().blockingGet()
        return Single.create<String> {
            val cmCertOrderId = CertOperations.getCmCertOrderId()
            val cmLogPhoneNo = CertOperations.getCmLogPhoneNo()
            val status = CertOperations.getCmLogUserStatus().name
            val json = CertOperations.getCmLogData(cmCertOrderId).blockingGet()
            val cmLogCertExpired = CertOperations.getCmLogCertExpired()

            val phoneInfo = PhoneInfo(
                    mobileAuthenStatus = status,
                    mobileAuthenOrderid = cmCertOrderId.toInt(),
                    mobileAuthenNumber = cmLogPhoneNo!!,
                    mobileAuthenCmData = json.base64(),
                    mobileAuthenExpired = cmLogCertExpired)

            val data = phoneInfo.toJson()
            val size = getDataFromSize(data.toByteArray(), MyCloudActivity.PHONE_OPERATOR, CM_NONCE)
            it.onSuccess(size)
        }
    }

    private fun createTnData(): Single<String> {
        TN_NONCE = IpfsOperations.getNonce().blockingGet()
        return Single.create<String> {
            val cmCertOrderId = worhavah.certs.tools.CertOperations.getTNCertOrderId()
            val cmLogPhoneNo = worhavah.certs.tools.CertOperations.getTnLogPhoneNo()
            val status = worhavah.certs.tools.CertOperations.getTNLogUserStatus().name
            val cmLogCertExpired = worhavah.certs.tools.CertOperations.getTNLogCertExpired()
            val js2 = worhavah.certs.tools.CertOperations.certPrefs.certTNLogData.get()!!.toByteArray()
            val phoneInfo = TNPhoneInfo(
                    sameCowMobileAuthenStatus = status,
                    sameCowMobileAuthenOrderid = cmCertOrderId.toString(),
                    sameCowMobileAuthenNumber = cmLogPhoneNo!!,
                    sameCowMobileAuthenCmData = js2.base64(),
                    sameCowMobileAuthenExpired = cmLogCertExpired.toString(),
                    sameCowMobileAuthenNonce = worhavah.certs.tools.CertOperations.certPrefs.certTNcertnonce.get().toString())

            val data = phoneInfo.toJson()
            val size = getDataFromSize(data.toByteArray(), MyCloudActivity.TNDATA, TN_NONCE)
            it.onSuccess(size)
        }
    }

    private fun createBankData(): Single<String> {
        BANK_NONCE = IpfsOperations.getNonce().blockingGet()
        return Single.create<String> {
            val info = CertOperations.getCertBankCardData()!!
            val expired = CertOperations.getBankCardCertExpired()
            val bankStatus = CertOperations.getBankStatus()

            val bankInfo = BankInfo(
                    bankAuthenStatus = bankStatus.first,
                    bankAuthenOrderid = bankStatus.second.toInt(),
                    bankAuthenName = info.bankName,
                    bankAuthenCode = info.bankCode,
                    bankAuthenCodeNumber = info.bankCardNo,
                    bankAuthenMobile = info.phoneNo,
                    bankAuthenExpired = expired)

            val data = bankInfo.toJson()
            val size = getDataFromSize(data.toByteArray(), MyCloudActivity.BANK_CARD_DATA, BANK_NONCE)
            it.onSuccess(size)
        }
    }

    private fun getDataFromSize(data: ByteArray, filename: String, nonce: BigInteger): String {
        val aesKey = passport.getIpfsAESKey()!!
        val hexString = Numeric.toHexString(nonce.toByteArray())
        val filetxt = File(rootpath, "$filename.txt")
        filetxt.ensureNewFile()
        val encrypt = AES256.encrypt(data, aesKey, hexString)
        filetxt.writeBytes(encrypt)
        val filezip = File(rootpath, "$filename.zip")
        ZipUtil.zip(filezip.absolutePath, filetxt.absolutePath)
        return filezip.formatSize()
    }

    fun upload(business: String, filename: String, status: (String, EventType) -> Unit, successful: (String) -> Unit, onError: (String, Throwable) -> Unit, onProgress: (String, Int) -> Unit) {
        val file = File(rootpath, "$filename.zip")
        IpfsCore.upload(file)
                .doProgress(business, 60, onProgress)
                .flatMap {
                    val ipfsKeyHash = passport.getIpfsKeyHash()!!
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            val idDigest = CertOperations.getLocalIdDigest()
                            IpfsOperations.putIpfsToken(business, it, idDigest.first, idDigest.second, ID_NONCE, ipfsKeyHash)
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            val bankDigest = CertOperations.getLocalBankDigest()
                            IpfsOperations.putIpfsToken(business, it, bankDigest.first, bankDigest.second, BANK_NONCE, ipfsKeyHash)
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            val cmDigest = CertOperations.getLocalCmDigest()
                            IpfsOperations.putIpfsToken(business, it, cmDigest.first, cmDigest.second, CM_NONCE, ipfsKeyHash)
                        }
                        ChainGateway.TN_COMMUNICATION_LOG -> {
                            val tnDigest = worhavah.certs.tools.CertOperations.getLocalTnDigest()
                            IpfsOperations.putIpfsToken(business, it, tnDigest.first, tnDigest.second, TN_NONCE, ipfsKeyHash)
                        }
                        else -> {
                            Single.error(Throwable())
                        }
                    }
                }
                .doMain()
                .doOnSubscribe(business, status, EventType.STATUS_UPLOADING, onProgress)
                .subscribeBy(
                        onSuccess = {
                            onProgress.invoke(business, 100)
                            successful.invoke(business)
                        },
                        onError = {
                            onError.invoke(business, it)
                        })
    }

    fun download(business: String, filename: String, status: (String, EventType) -> Unit, successful: (String) -> Unit, onError: (Throwable) -> Unit, onProgress: (String, Int) -> Unit) {
        var nonce = BigInteger("0")
        IpfsOperations.getIpfsToken(business)
                .map {
                    FunctionReturnDecoder.decode(it.result, Erc20Helper.decodeTokenResponse())
                }
                .doProgress(business, 20, onProgress)
                .flatMap {
                    //获取nonce 和 token
                    if (IpfsOperations.VERSION.toInt() < (it[2] as Uint256).value.toInt()) {
                        Single.error(DccChainServiceException("云存储数据加密算法已升级，为确保该功能可以继续使用，请更新APP到最新版本。"))
                    } else {
                        nonce = BigInteger((it[5] as DynamicBytes).value)
                        Single.just((it[4] as Utf8String).value)
                    }
                }
                .flatMap {
                    //ipfs下载
                    IpfsCore.download(it)
                }
                .doProgress(business, 60, onProgress)
                .map {
                    //写入zip
                    val file = File(rootpath, "$filename.zip")
                    file.ensureNewFile()
                    file.writeBytes(it)
                    file
                }
                .map {
                    //解压zip
                    ZipUtil.unZip(it.absolutePath, rootpath)
                }
                .doProgress(business, 80, onProgress)
                .map {
                    //解密文件
                    val aesKey = passport.getIpfsAESKey()!!
                    val hexString = Numeric.toHexString(nonce.toByteArray())
                    val filetxt = File(rootpath, "$filename.txt")
                    val decrypt = AES256.decrypt(filetxt.readBytes(), aesKey, hexString)
                    String(decrypt)
                }
                .doOnSuccess {
                    //成功后写入数据
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            val idInfo = it.toBean(IdInfo::class.java)
                            CertOperations.saveIpfsIdData(idInfo)
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            val bankInfo = it.toBean(BankInfo::class.java)
                            CertOperations.saveIpfsBankData(bankInfo)
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            val phoneInfo = it.toBean(PhoneInfo::class.java)
                            CertOperations.saveIpfsCmData(phoneInfo)
                        }
                        ChainGateway.TN_COMMUNICATION_LOG -> {
                            val phoneInfo = it.replace("\n", "").toBean(TNPhoneInfo::class.java)
                            CertOperations.saveIpfsTNData(phoneInfo)
                        }
                    }
                    delectedFile(filename)
                }
                .doMain()
                .doOnSubscribe(business, status, EventType.STATUS_DOWNLOADING, onProgress)
                .subscribeBy(
                        onSuccess = {
                            onProgress.invoke(business, 100)
                            successful.invoke(business)
                        },
                        onError = {
                            onError.invoke(it)
                        })
    }

    private fun delectedFile(filename: String) {
        FileUtils.deleteFile(rootpath + File.separator + "$filename.txt")
        FileUtils.deleteFile(rootpath + File.separator + "$filename.zip")
    }

    private fun <T> Single<T>.doOnSubscribe(business: String, status: (String, EventType) -> Unit, text: EventType, onProgress: (String, Int) -> Unit): Single<T> {
        return this.doOnSubscribe {
            status.invoke(business, text)
            onProgress.invoke(business, 0)
        }
    }

    private fun <T> Single<T>.doProgress(business: String, progress: Int, onProgress: (String, Int) -> Unit): Single<T> {
        return this.doMain()
                .map {
                    onProgress.invoke(business, progress)
                    it
                }
                .doBack()
    }
}
