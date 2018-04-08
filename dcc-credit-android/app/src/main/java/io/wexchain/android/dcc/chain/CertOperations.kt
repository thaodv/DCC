package io.wexchain.android.dcc.chain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.pair
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CertOrder
import io.wexchain.dccchainservice.domain.IdOcrInfo
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

object CertOperations {

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
                    val digest1 = MessageDigest.getInstance("SHA256").digest(data)
                    val digest2 = MessageDigest.getInstance("SHA256").run {
                        update(digest1)
                        digest(MessageDigest.getInstance("SHA256").digest(photo))
                    }
                    EthsFunctions.apply(digest1, digest2, BigInteger.valueOf(IdCardEssentialData.ID_TIME_EXPIRATION_UNLIMITED))
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

}