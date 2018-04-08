package io.wexchain.auth

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.reactivex.SingleTransformer
import io.wexchain.android.common.toHex
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.EthsFunctions
import io.wexchain.android.dcc.chain.txSigned
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.BankCodes
import io.wexchain.dccchainservice.domain.CertStatus
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.web3j.crypto.Credentials
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
    }

    lateinit var credentials: Credentials
    lateinit var chainGateway: ChainGateway
    lateinit var certApi: CertApi

    @Before
    fun setup() {
        credentials = Credentials.create("f233101e5e25949640694a0e640f6027688d5d4a62ef3c094495e7d689499177")
        InstrumentationRegistry.getTargetContext()
        chainGateway = App.get().chainGateway
        certApi = App.get().certApi
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
                .flatMap {txHash->
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
                        file = CertApi.uploadFilePart(mockPic, "user.jpg", "image/jpeg"),
                        signature = ParamSignatureUtil.sign(keyPair.private, mapOf<String, String>(
                                "address" to credentials.address,
                                "orderId" to order.orderId.toString(),
                                "realName" to name,
                                "certNo" to id
                        ))
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
        val bankCode = BankCodes.ICBC
        val bankAccountNo = "6202123443211234"
        val accountName = name
        val certNo = id
        val phoneNo = "13867899876"

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
                                        ticket.ticket, tx, ticket.answer, business)
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
        val sigStr = params.entries.sortedBy { it.key }.joinToString(separator = "&") { "${it.key}=${it.value}" }
        println("sigStr = $sigStr")
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
                                println(transactionMessage)
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