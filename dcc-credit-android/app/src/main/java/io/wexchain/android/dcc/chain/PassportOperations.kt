package io.wexchain.android.dcc.chain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.getAndroidKeyStoreLoaded
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.EthsFunctions.deleteKey
import io.wexchain.android.dcc.chain.EthsHelper.ANDROID_RSA_PREFIX
import io.wexchain.android.dcc.chain.EthsHelper.createAndroidRSAKeyPair
import io.wexchain.android.dcc.domain.AuthKey
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.TicketResponse
import org.web3j.crypto.Credentials
import java.util.*

/**
 * Created by lulingzhi on 2017/11/24.
 */
object PassportOperations {

    fun createNewAndEnablePassport(password: String): Single<Pair<Credentials, AuthKey>> {
        require(password.isNotBlank())
        return Single.just(password)
                .observeOn(Schedulers.computation())
                .map {
                    EthsHelper.createNewCredential()
                }
                .map {
                    val authKey = EthsHelper.createAndroidRSAKeyPair().let {
                        AuthKey(it.second, it.first.public.encoded)
                    }
                    it to authKey
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    App.get().chainGateway.getTicket()
                            .compose(Result.checked())
                            .flatMap { ticket ->
                                PassportOperations.uploadPubKeyChecked(it, ticket.ticket, null)
                            }
                }
                .doOnSuccess {
                    App.get().passportRepository.savePassport(it.first, password, it.second)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun enablePassport(credentials: Credentials,password: String): Single<Pair<Credentials, AuthKey>>? {
        require(password.isNotBlank())
        return Single.just(credentials)
                .observeOn(Schedulers.computation())
                .map {
                    val authKey = EthsHelper.createAndroidRSAKeyPair().let {
                        AuthKey(it.second, it.first.public.encoded)
                    }
                    it to authKey
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    App.get().chainGateway.getTicket()
                            .compose(Result.checked())
                            .flatMap { ticket ->
                                PassportOperations.uploadPubKeyChecked(it, ticket.ticket, null)
                            }
                }
                .doOnSuccess {
                    App.get().passportRepository.savePassport(it.first, password, it.second)
                }
                .observeOn(AndroidSchedulers.mainThread())

    }

    /**
     * 3 step chain ops:
     * 1. upload pub key
     * 2. hasReceipt
     * 3. get pub key
     */
    private fun uploadPubKeyChecked(
            authKey: AuthKey,
            credentials: Credentials,
            ticket: String,
            code: String?
    ): Single<AuthKey> {
        val pubKey = authKey.publicKeyEncoded
        val api = App.get().chainGateway
        return api.getCaContractAddress()
                .compose(Result.checked())
                .flatMap {
                    val transactionMessage = EthsFunctions.putKey(pubKey).txSigned(credentials = credentials, address = it)
                    api.uploadCaPubKey(ticket, transactionMessage, code)
                }
                .compose(Result.checked())
                .compose(checkTxOnChain(credentials.address, authKey))
    }

    /**
     * call on io scheduler
     */
    private fun checkTxOnChain(address: String, authKey: AuthKey): SingleTransformer<String, AuthKey> {
        val api = App.get().chainGateway
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
                                    val decodedPubKey = Base64.decode(it, Base64.DEFAULT)
                                    if (Arrays.equals(decodedPubKey, authKey.publicKeyEncoded)) {
                                        Single.just(authKey)
                                    } else {
                                        Single.error<AuthKey>(IllegalStateException())
                                    }
                                }
                    }
        }
    }

    fun uploadPubKeyChecked(
            pair: Pair<Credentials, AuthKey>,
            ticket: String,
            code: String?
    ): Single<Pair<Credentials, AuthKey>> {
        val credentials = pair.first
        return uploadPubKeyChecked(pair.second, credentials, ticket, code)
                .map { pair }
    }

    private fun createPubKey(): Single<AuthKey> {
        return Single.fromCallable { createAndroidRSAKeyPair() }
                .map { AuthKey(it.second, it.first.public.encoded) }
                .subscribeOn(Schedulers.computation())
    }

    fun deletePubKeyAndUploadChecked(passport: Passport, ticket: String, code: String?): Single<Passport> {
        val api = App.get().chainGateway
        return api.getCaContractAddress()
                .compose(Result.checked())
                .observeOn(Schedulers.computation())
                .map {
                    val credential = passport.credential
                    deleteKey().txSigned(credentials = credential, address = it)
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    api.deleteCaPubKey(ticket, it, code)
                }
                .compose(Result.checked())
                .flatMap {
                    api.hasReceipt(it)
                            .compose(Result.checked())
                            .flatMap { if (it) Single.just(passport) else Single.error(IllegalStateException()) }
                            .retryWhen(RetryWithDelay.createSimple(4, 5000L))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    App.get().passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(passport.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.DISABLE))
                }
    }

    /**
     * from ca api
     */
    fun loadTicketDecoded(): Single<Pair<TicketResponse, Bitmap?>> {
        return App.get().chainGateway.getTicket()
                .compose(Result.checked())
                .observeOn(Schedulers.computation())
                .map { Pair(it, it.decodePng()) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun TicketResponse.decodePng(): Bitmap? {
        image ?: return null
        val decode = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decode, 0, decode.size)
    }

    fun deleteAllLocalAndroidRSAKeys() {
        val ks = getAndroidKeyStoreLoaded()
        val list = ks.aliases().toList()
        list.filter { it.startsWith(ANDROID_RSA_PREFIX) }.forEach {
            ks.deleteEntry(it)
        }
    }
}
