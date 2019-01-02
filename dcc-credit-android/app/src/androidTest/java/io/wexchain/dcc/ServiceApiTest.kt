package io.wexchain.dcc

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.JsonSyntaxException
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.wexchain.android.common.toHex
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations.certOrderByTx
import io.wexchain.android.dcc.chain.EthsFunctions
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.chain.privateChainNonce
import io.wexchain.android.dcc.chain.txSigned
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.pair
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.ScfApi
import io.wexchain.dccchainservice.domain.CertStatus
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.util.*

@RunWith(AndroidJUnit4::class)
class ServiceApiTest {

    companion object {
        const val name = "张三"
        const val id = "430726199407035439"
        const val phoneNo = "13867899876"
        const val bankCode = "ICBC"
        const val bankAccountNo = "6202123443211234"
    }

    lateinit var credentials: Credentials
    lateinit var chainGateway: ChainGateway
    lateinit var certApi: CertApi
    lateinit var scfApi: ScfApi
    lateinit var scfTestApi: ScfTestApi

    @Before
    fun setup() {
        credentials = Credentials.create(Keys.createEcKeyPair())
        InstrumentationRegistry.getTargetContext()
        chainGateway = App.get().chainGateway
        certApi = App.get().certApi
        scfApi = App.get().scfApi
        scfTestApi = App.get().networking.createApi(ScfTestApi::class.java, BuildConfig.DCC_MARKETING_API_URL)
    }

    @Test
    fun testScfLogin() {
        val keyPair = createKeyPair()
        uploadCaKey(keyPair)
        val address = credentials.address
        val scfTokenManager = App.get().scfTokenManager
        scfTokenManager.scfToken = "invalid_token"
        val response = ScfOperations.currentToken
                .flatMap {
                    scfTestApi.testPing(it)
                            .map {
                                if(it.isSuccessful){
                                    val body = it.body()
                                    if (body == null){
                                        //ok
                                        it
                                    }else{
                                        if (body.isSuccess){
                                            it//ok
                                        }else{
                                            throw body.asError()
                                        }
                                    }
                                }else{
                                    throw IllegalStateException()
                                }
                            }
                            .map { "pong" }
                            .onErrorReturn {
                                if(it is JsonSyntaxException){
                                    "pong"
                                }else throw it
                            }
                }
                .compose(ScfOperations.withScfToken(address, keyPair.private))
                .blockingGet()
        println(response)
        scfTokenManager.scfToken = null

//        val response = scfApi.getNonce()
//                .compose(Result.checked())
//                .flatMap {
//                    scfApi.login(
//                            nonce = it,
//                            address = address,
//                            username = address,
//                            password = null,
//                            sign = ParamSignatureUtil.sign(keyPair.private, mapOf(
//                                    "nonce" to it,
//                                    "address" to address,
//                                    "username" to address,
//                                    "password" to null
//                            ))
//                    )
//                }
//                .blockingGet()
//        val token = response.headers()["x-auth-token"]!!
//        println(token)
//        val response1 = scfTestApi.testPing(token).blockingGet()
//        println(response1.body())
    }

    @Test
    fun testCa() {
        val keyPair = createKeyPair()
        uploadCaKey(keyPair)
    }

    @Test
    fun testIdVerify() {
        val keyPair = createKeyPair()
        uploadCaKey(keyPair)
        val data = name.toByteArray(Charsets.UTF_8) + id.toByteArray(Charsets.UTF_8)
        val mockPic = ByteArray(10000).apply { Random().nextBytes(this) }
        val digest1 = MessageDigest.getInstance("SHA256").digest(data)
        val digest2 = MessageDigest.getInstance("SHA256").run {
            update(digest1)
            digest(MessageDigest.getInstance("SHA256").digest(mockPic))
        }
        val api = chainGateway
        val business = ChainGateway.BUSINESS_ID
        val order = api.getCertContractAddress(business)
                .compose(Result.checked())
                .flatMap { contractAddress ->
                    api.getTicket()
                            .compose(Result.checked())
                            .flatMap { ticket ->
                                val tx = EthsFunctions.apply(digest1, digest2, BigInteger.valueOf(IdCardEssentialData.ID_TIME_EXPIRATION_UNLIMITED))
                                        .txSigned(credentials, contractAddress)
                                api.certApply(
                                        ticket.ticket, tx, ticket.answer, business)
                                        .compose(Result.checked())
                            }
                }
                .flatMap { txHash ->
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
                .blockingGet()
        println(order)
        Assert.assertArrayEquals(digest1, android.util.Base64.decode(order.content.digest1, android.util.Base64.DEFAULT))
        Assert.assertArrayEquals(digest2, android.util.Base64.decode(order.content.digest2, android.util.Base64.DEFAULT))
        val verifyOrder = certApi
                .idVerify(
                        address = credentials.address,
                        orderId = order.orderId,
                        realName = name,
                        certNo = id,
                        personalPhoto = CertApi.uploadFilePart(mockPic, "user.jpg", "image/jpeg","personalPhoto"),
                        frontPhoto = CertApi.uploadFilePart(mockPic, "front.jpg", "image/jpeg","frontPhoto"),
                        backPhoto = CertApi.uploadFilePart(mockPic, "back.jpg", "image/jpeg","backPhoto"),
                        signature = ParamSignatureUtil.sign(keyPair.private, mapOf<String, String>(
                                "address" to credentials.address,
                                "orderId" to order.orderId.toString(),
                                "realName" to name,
                                "certNo" to id,
                                "version" to "1.1.0"
                        )),
                        version = "1.1.0"
                )
                .compose(Result.checked())
                .blockingGet()
        println(verifyOrder)
        val state = api.getCertData(credentials.address, business)
                .compose(Result.checked())
//                .retryWhen(RetryWithDelay.createSimple(4, 5000))
                .blockingGet()
        println(state)
    }

    @Test
    fun testBankVerify() {
        val accountName = name
        val certNo = id

        val keyPair = createKeyPair()
        uploadCaKey(keyPair)
        val api = chainGateway
        val business = ChainGateway.BUSINESS_BANK_CARD

        val data = bankAccountNo.toByteArray(Charsets.UTF_8) + phoneNo.toByteArray(Charsets.UTF_8)
        val digest1 = MessageDigest.getInstance("SHA256").digest(data)
        val digest2 = byteArrayOf()
        val applyOrder = api.getCertContractAddress(business)
                .compose(Result.checked())
                .flatMap { contractAddress ->
                    api.getTicket()
                            .compose(Result.checked())
                            .flatMap { ticket ->
                                val tx = EthsFunctions.apply(digest1, digest2, BigInteger.ZERO)
                                        .txSigned(credentials, contractAddress)
                                api.certApply(
                                        ticket.ticket, tx, null, business)
                                        .compose(Result.checked())
                            }
                }
                .flatMap {
                    api.getOrderByTx(it, business)
                            .compose(Result.checked())
                            .retryWhen(RetryWithDelay.createSimple(4, 5000L))

                }
                .blockingGet()
        println(applyOrder)
        Assert.assertArrayEquals(digest1, android.util.Base64.decode(applyOrder.content.digest1, android.util.Base64.DEFAULT))
        Assert.assertArrayEquals(digest2, android.util.Base64.decode(applyOrder.content.digest2, android.util.Base64.DEFAULT))
        val params = mapOf(
                "address" to credentials.address,
                "orderId" to applyOrder.orderId.toString(),
                "bankCode" to bankCode,
                "bankAccountNo" to bankAccountNo,
                "accountName" to accountName,
                "certNo" to certNo,
                "phoneNo" to phoneNo
        )
        val signature = ParamSignatureUtil.sign(keyPair.private, params)
        val verifyOrder = certApi
                .bankCardVerify(
                        address = credentials.address,
                        orderId = applyOrder.orderId,
                        bankCode = bankCode,
                        bankAccountNo = bankAccountNo,
                        accountName = accountName,
                        certNo = certNo,
                        phoneNo = phoneNo,
                        signature = signature
                )
                .compose(Result.checked())
                .blockingGet()
        println(verifyOrder)
        val state = api.getOrderByOrderId(applyOrder.orderId, business)
                .compose(Result.checked())
                .map {
                    if (it.status == CertStatus.APPLIED) {
                        throw IllegalStateException("not verified yet")
                    }
                    it
                }
                .retryWhen(RetryWithDelay.createSimple(4, 5000))
                .blockingGet()
        println(state)
    }

    @Test
    fun testCommunicationLogSubmit() {
        val keyPair = createKeyPair()
        uploadCaKey(keyPair)
        val api = chainGateway
        val business = ChainGateway.BUSINESS_COMMUNICATION_LOG
        val nonce = privateChainNonce(credentials.address)
        val certOrder = Single
                .zip(
                        api.getCertContractAddress(business).compose(Result.checked()),
                        api.getTicket().compose(Result.checked()),
                        pair()
                )
                .flatMap { (contractAddress, ticket) ->
                    val tx = EthsFunctions.apply(byteArrayOf(), byteArrayOf(), BigInteger.ZERO).txSigned(credentials, contractAddress, nonce)
                    api.certApply(ticket.ticket, tx, null, business)
                            .compose(Result.checked())
                }
                .certOrderByTx(api, business)
                .blockingGet()
        val cl = certApi.requestCommunicationLogData(
                address = credentials.address,
                orderId = certOrder.orderId,
                userName = name,
                certNo = id,
                phoneNo = phoneNo,
                password = "123456",
                signature = ParamSignatureUtil.sign(keyPair.private, mapOf(
                        "address" to credentials.address,
                        "orderId" to certOrder.orderId.toString(),
                        "userName" to name,
                        "certNo" to id,
                        "phoneNo" to phoneNo,
                        "password" to "123456"
                ))
        ).compose(Result.checked())
                .blockingGet()
        println(cl)

    }

    private fun uploadCaKey(keyPair: KeyPair) {
        val api = chainGateway
        val pubKey = keyPair.public.encoded
        val onChain = api.getTicket()
                .compose(Result.checked())
                .flatMap { ticket ->
                    api.getCaContractAddress()
                            .compose(Result.checked())
                            .flatMap {
                                val transactionMessage = EthsFunctions.putKey(pubKey).txSigned(credentials = credentials, address = it)
//                                println(transactionMessage)
                                api.uploadCaPubKey(ticket.ticket, transactionMessage, ticket.answer)
                            }
                            .compose(Result.checked())
                }
                .compose(api.checkCaOnChain(credentials.address, pubKey))
                .blockingGet()
        println(onChain.toHex())
    }

    private fun createKeyPair(): KeyPair {
        val keyPair = KeyPairGenerator.getInstance("RSA").run {
            initialize(2048)
            genKeyPair()
        }
        return keyPair!!
    }


    private fun ChainGateway.checkCaOnChain(address: String, data: ByteArray): SingleTransformer<String, ByteArray> {
        val api = this
        return SingleTransformer {
            it
                    .flatMap {
                        api.hasReceipt(it)
                                .compose(Result.checked())
                                .map {
                                    if (!it) {
                                        throw DccChainServiceException("no receipt yet")
                                    }
                                    it
                                }
                                .retryWhen(RetryWithDelay.createSimple(4, 5000L))
                    }
                    .flatMap {
                        api.getPubKey(address)
                                .compose(Result.checked())
                                .flatMap {
                                    val decodedPubKey = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
                                    if (java.util.Arrays.equals(decodedPubKey, data)) {
                                        io.reactivex.Single.just(data)
                                    } else {
                                        io.reactivex.Single.error<ByteArray>(IllegalStateException())
                                    }
                                }
                    }
        }
    }
}
