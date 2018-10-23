package worhavah.regloginlib.tools

import android.content.Context
import android.util.Base64
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.getAndroidKeyStoreLoaded
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import org.web3j.crypto.Credentials
import worhavah.regloginlib.AuthKey
import worhavah.regloginlib.EthsHelper
import worhavah.regloginlib.EthsHelper.ANDROID_RSA_PREFIX
import worhavah.regloginlib.EthsHelper.createAndroidRSAKeyPair
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import worhavah.regloginlib.PassportRepository
import java.util.*

/**
 * Created by lulingzhi on 2017/11/24.
 */
object PassportOperations {

    fun ensureCaValidity(activity: BaseCompatActivity, action: () -> Unit) {

        val passport = Networkutils.passportRepository.getCurrentPassport()
        if (passport == null) {
            Pop.toast("钱包不存在", activity)
            return
        }
        val authKey = passport.authKey
        if (authKey == null) {
            Single.error<AuthKey>(IllegalStateException("未启用统一登录"))
        } else {
            Networkutils.chainGateway.getPubKey(passport.address)
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
                activity.toast("您当前使用的统一登录秘钥与链上不一致,请前往首页更新密钥")
                CustomDialog(activity).apply {
                    this.setTitle("提示")
                    textContent = it.message
                    withPositiveButton("更新") {
                      //  activity.navigateTo(AuthManageActivity::class.java)
                        true
                    }
                    withNegativeButton()
                }.assembleAndShow()
            })
    }
  fun updatePubKeyAndUploadChecked(passport: Passport, ticket: String, code: String?): Single<AuthKey> {
      return createPubKey()
          .flatMap {
              uploadPubKeyChecked(it, passport.credential, ticket, code)
          }
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSuccess {
              PassportRepository.updateAuthKey(passport,it)
            //  PassportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(passport.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.UPDATE))
          }
  }
    /**
     * 3 step chain ops:
     * 1. upload pub key
     * 2. hasReceipt
     * 3. get pub key
     */
      fun uploadPubKeyChecked(
        authKey: AuthKey,
        credentials: Credentials,
        ticket: String,
        code: String?
    ): Single<AuthKey> {
        val pubKey = authKey.publicKeyEncoded
        val api = Networkutils.chainGateway
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
        val api = Networkutils.chainGateway
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
    private fun createPubKey(): Single<AuthKey> {
        return Single.fromCallable { createAndroidRSAKeyPair() }
            .map { AuthKey(it.second, it.first.public.encoded) }
            .subscribeOn(Schedulers.computation())
    }

    fun createNewAndEnablePassport(password: String,activity:Context): Single<Pair<Credentials, AuthKey>> {
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
                 //   Networkutils.passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE))
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun enablePassport(credentials: Credentials, password: String,activity:Context): Single<Pair<Credentials, AuthKey>>? {
        require(password.isNotBlank())
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
                    Networkutils.passportRepository.saveNewPassport(it.first, password, it.second)
                  //  Networkutils.passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(it.first.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.ENABLE))
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
            code: String?,activity:Context
    ): Single<AuthKey> {
        val pubKey = authKey.publicKeyEncoded
        val api = Networkutils.chainGateway
        return api.getCaContractAddress()
                .compose(Result.checked())
                .flatMap {
                    val transactionMessage = EthsFunctions.putKey(pubKey).txSigned(credentials = credentials, address = it)
                    api.uploadCaPubKey(ticket, transactionMessage, code)
                }
                .compose(Result.checked())
                .compose(checkTxOnChain(credentials.address, authKey,activity))
    }

    /**
     * call on io scheduler
     */
    private fun checkTxOnChain(address: String, authKey: AuthKey,activity:Context): SingleTransformer<String, AuthKey> {
        val api = Networkutils.chainGateway
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
            code: String?,activity:Context
    ): Single<Pair<Credentials, AuthKey>> {
        val credentials = pair.first
        return uploadPubKeyChecked(pair.second, credentials, ticket, code,activity)
                .map { pair }
    }

    /*private fun createPubKey(): Single<AuthKey> {
        return Single.fromCallable { createAndroidRSAKeyPair() }
                .map { AuthKey(it.second, it.first.public.encoded) }
                .subscribeOn(Schedulers.computation())
    }*/

    /*fun updatePubKeyAndUploadChecked(passport: Passport, ticket: String, code: String?): Single<AuthKey> {
        return createPubKey()
                .flatMap {
                    uploadPubKeyChecked(it, passport.credential, ticket, code)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    App.get().passportRepository.updateAuthKey(passport,it)
                    App.get().passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(passport.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.UPDATE))
                }
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
                    App.get().passportRepository.updateAuthKey(passport,null)
                    App.get().passportRepository.addAuthKeyChangedRecord(AuthKeyChangeRecord(passport.address, System.currentTimeMillis(), AuthKeyChangeRecord.UpdateType.DISABLE))
                }
    }*/

    /**
     * from ca api
     */
    /*fun loadTicketDecoded(): Single<Pair<TicketResponse, Bitmap?>> {
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
    }*/

    fun deleteAllLocalAndroidRSAKeys() {
        val ks = getAndroidKeyStoreLoaded()
        val list = ks.aliases().toList()
        list.filter { it.startsWith(ANDROID_RSA_PREFIX) }.forEach {
            ks.deleteEntry(it)
        }
    }
}
