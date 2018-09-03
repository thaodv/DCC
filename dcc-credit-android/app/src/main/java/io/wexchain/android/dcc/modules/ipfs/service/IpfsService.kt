package io.wexchain.android.dcc.modules.ipfs.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.ensureNewFile
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
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
import io.wexchain.ipfs.utils.AES256
import io.wexchain.ipfs.utils.ZipUtil
import io.wexchain.ipfs.utils.base64
import io.wexchain.ipfs.utils.doMain
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

    fun createIdData(onSize: (String) -> Unit) {
        ID_NONCE = IpfsOperations.getNonce().blockingGet()
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

        val json = idInfo.toJson()
        val data = json.toByteArray()

        val size = getDataFromSize(data, MyCloudActivity.ID_ENTIFY_DATA, ID_NONCE)
        onSize.invoke(size)
    }

    fun createCmData(onSize: (String) -> Unit) {
        CM_NONCE = IpfsOperations.getNonce().blockingGet()
        val cmCertOrderId = CertOperations.getCmCertOrderId()
        val cmLogPhoneNo = CertOperations.getCmLogPhoneNo()
        val status = CertOperations.getCmLogUserStatus().name
        val json = CertOperations.getCmLogData(cmCertOrderId).blockingGet()

        val phoneInfo = PhoneInfo(
                mobileAuthenStatus = status,
                mobileAuthenOrderid = cmCertOrderId.toInt(),
                mobileAuthenNumber = cmLogPhoneNo!!,
                mobileAuthenCmData = json.base64())

        val data = phoneInfo.toJson()
        val size = getDataFromSize(data.toByteArray(), MyCloudActivity.PHONE_OPERATOR, CM_NONCE)
        onSize.invoke(size)
    }

    fun createBankData(onSize: (String) -> Unit) {
        BANK_NONCE = IpfsOperations.getNonce().blockingGet()
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
                bankAuthenExpired = expired)

        val data = bankInfo.toJson()
        val size = getDataFromSize(data.toByteArray(), MyCloudActivity.BANK_CARD_DATA, BANK_NONCE)
        onSize.invoke(size)
    }

    private fun getDataFromSize(data: ByteArray, filename: String, nonce: BigInteger): String {
        val ipfsKeyHash = passport.getIpfsKeyHash()!!
        val hexString = Numeric.toHexString(nonce.toByteArray())
        val filetxt = File(rootpath, "$filename.txt")
        filetxt.ensureNewFile()
        val encrypt = AES256.encrypt(data, ipfsKeyHash, hexString)
        filetxt.writeBytes(encrypt)
        val filezip = File(rootpath, "$filename.zip")
        ZipUtil.zip(filezip.absolutePath, filetxt.absolutePath)
        return filezip.formatSize()
    }

    fun upload(business: String, filename: String, status: (String, String) -> Unit, successful: (String) -> Unit, onError: (String, Throwable) -> Unit) {
        val file = File(rootpath, "$filename.zip")
        IpfsCore.upload(file)
                .flatMap {
                    when (business) {
                        ChainGateway.BUSINESS_ID -> {
                            val idDigest = CertOperations.getLocalIdDigest()
                            IpfsOperations.putIpfsToken(business, it, idDigest.first, idDigest.second, ID_NONCE)
                        }
                        ChainGateway.BUSINESS_BANK_CARD -> {
                            val idDigest = CertOperations.getLocalBankDigest()
                            IpfsOperations.putIpfsToken(business, it, idDigest.first, idDigest.second, BANK_NONCE)
                        }
                        ChainGateway.BUSINESS_COMMUNICATION_LOG -> {
                            val idDigest = CertOperations.getLocalCmDigest()
                            IpfsOperations.putIpfsToken(business, it, idDigest.first, idDigest.second, CM_NONCE)
                        }
                        else -> {
                            Single.error(Throwable())
                        }
                    }
                }
                .doMain()
                .doOnSubscribe(business, status, "正在上传")
                .subscribeBy(
                        onSuccess = {
                            successful(business)
                        },
                        onError = {
                            onError.invoke(business, it)
                        })
    }

    fun download(business: String, filename: String, status: (String, String) -> Unit, successful: (String) -> Unit, onError: (Throwable) -> Unit) {
        var nonce = BigInteger("0")
        IpfsOperations.getIpfsToken(business)
                .map {
                    FunctionReturnDecoder.decode(it.result, Erc20Helper.dncodeResponse())
                }
                .flatMap {
                    //获取nonce 和 token
                    if (IpfsOperations.VERSION.toInt() < (it[2] as Uint256).value.toInt()) {
                        Single.error(DccChainServiceException("云存储数据加密算法已升级，为确保该功能可以继续使用，请更新APP到最新版本。"))
                    } else {
                        nonce = BigInteger((it[4] as DynamicBytes).value)
                        Single.just((it[5] as Utf8String).value)
                    }
                }
                .flatMap {
                    //ipfs下载
                    IpfsCore.download(it)
                }
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
                .map {
                    //解密文件
                    val ipfsKeyHash = passport.getIpfsKeyHash()!!
                    val hexString = Numeric.toHexString(nonce.toByteArray())
                    val filetxt = File(rootpath, "$filename.txt")
                    val decrypt = AES256.decrypt(filetxt.readBytes(), ipfsKeyHash, hexString)
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
                        else -> {
                        }
                    }
                    delectedFile(filename)
                }
                .doMain()
                .doOnSubscribe(business, status, "正在下载")
                .subscribeBy(
                        onSuccess = {
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

    fun <T> Single<T>.doOnSubscribe(business: String, status: (String, String) -> Unit, text: String): Single<T> {
        return this.doOnSubscribe {
            status.invoke(business, text)
        }
    }

}