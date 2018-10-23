package io.wexchain.android.dcc.chain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.getAndroidKeyStoreLoaded
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.PassportActivity
import io.wexchain.android.dcc.chain.EthsFunctions.deleteKey
import io.wexchain.android.dcc.chain.EthsHelper.ANDROID_RSA_PREFIX
import io.wexchain.android.dcc.chain.EthsHelper.createAndroidRSAKeyPair
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.TicketResponse
import org.web3j.crypto.Credentials
import java.util.*
import io.wexchain.android.dcc.domain.AuthKey
import worhavah.regloginlib.Net.Networkutils

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
                       worhavah.regloginlib .AuthKey(it.second, it.first.public.encoded)
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
                    App.get().passportRepository.saveNewPassport(it.first, password, it.second)
                    Networkutils.passportRepository.saveNewPassport(it.first, password,  worhavah.regloginlib.AuthKey(it.second.keyAlias,it.second.publicKeyEncoded))
                    App.get().passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE))
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun ensureCaValidity(activity: BaseCompatActivity, action: () -> Unit) {
        val app = App.get()
        val passport = app.passportRepository.getCurrentPassport()
        if (passport == null) {
            Pop.toast("钱包不存在", activity)
            return
        }
        val authKey = passport.authKey
        if (authKey == null) {
            Single.error<AuthKey>(IllegalStateException("未启用统一登录"))
        } else {
            app.chainGateway.getPubKey(passport.address)
                .compose(Result.checked())
                .flatMap {
                    val decodedPubKey = Base64.decode(it, Base64.DEFAULT)
                    if (Arrays.equals(decodedPubKey, authKey.publicKeyEncoded)) {
                        Single.just(authKey)
                    } else {
                        Single.error<AuthKey>(IllegalStateException("您当前使用的统一登录秘钥与链上不一致,将影响部分功能的正常使用"))
                    }
                }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                activity.showLoadingDialog()
            }
            .doFinally {
                activity.hideLoadingDialog()
            }
            .subscribe({
                action()
            }, {
                CustomDialog(activity).apply {
                    this.setTitle(io.wexchain.android.dcc.tools.getString(R.string.tips))
                    textContent = it.message
                    withPositiveButton(io.wexchain.android.dcc.tools.getString(R.string.update)) {
                        activity.navigateTo(PassportActivity::class.java)
                        true
                    }
                    withNegativeButton()
                }.assembleAndShow()
            })
    }

    fun enablePassport(credentials: Credentials, password: String): Single<Pair<Credentials, AuthKey>>? {
        require(password.isNotBlank())
        return Single.just(credentials)
                .observeOn(Schedulers.computation())
                .map {
                    val authKey = EthsHelper.createAndroidRSAKeyPair().let {
                        worhavah.regloginlib .AuthKey(it.second, it.first.public.encoded)
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
                    App.get().passportRepository.saveNewPassport(it.first, password, it.second)
                    Networkutils.passportRepository.saveNewPassport(it.first, password,  worhavah.regloginlib.AuthKey(it.second.keyAlias,it.second.publicKeyEncoded))
                    App.get().passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE))
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
        worhavah.regloginlib.tools.PassportOperations.uploadPubKeyChecked( worhavah.regloginlib.AuthKey(pair.second.keyAlias,pair.second.publicKeyEncoded), credentials, ticket, code)
        return uploadPubKeyChecked(pair.second, credentials, ticket, code)
                .map { pair }
    }

    private fun createPubKey(): Single<AuthKey> {
        return Single.fromCallable { createAndroidRSAKeyPair() }
                .map {
                    worhavah.regloginlib .AuthKey(it.second, it.first.public.encoded)
                    AuthKey(it.second, it.first.public.encoded) }
                .subscribeOn(Schedulers.computation())
    }

    fun updatePubKeyAndUploadChecked(passport: Passport, ticket: String, code: String?): Single<AuthKey> {
        return createPubKey()
                .flatMap {
                    uploadPubKeyChecked(it, passport.credential, ticket, code)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    App.get().passportRepository.updateAuthKey(passport, it)
                    App.get().passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(passport.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.UPDATE))
                }
    }

    fun switchAuthkey(it:AuthKey): worhavah.regloginlib.AuthKey {
        return worhavah.regloginlib.AuthKey(it.keyAlias,it.publicKeyEncoded)
    }
    fun switchPass(it: Passport): worhavah.regloginlib.Passport {
        return worhavah.regloginlib.Passport(it.credential,switchAuthkey(it.authKey!!),it.nickname,it.avatarUri)
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
                    App.get().passportRepository.updateAuthKey(passport, null)
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
