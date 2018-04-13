package io.wexchain.android.dcc.repo

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.WorkerThread
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Prefs
import io.wexchain.android.common.switchMap
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.ChooseCutImageActivity
import io.wexchain.android.dcc.chain.EthsHelper
import io.wexchain.android.dcc.domain.AuthKey
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.dcc.repo.db.CaAuthRecord
import io.wexchain.android.dcc.repo.db.PassportDao
import io.wexchain.android.dcc.tools.RoomHelper
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.crypto.WalletFileDeserializer
import org.web3j.utils.Numeric
import java.io.File

/**
 *
 */
class PassportRepository(context: Context,
                         val dao: PassportDao) {


    fun addAuthKeyChangedRecord(authKeyChangeRecord: AuthKeyChangeRecord) {
        RoomHelper.onRoomIoThread {
            dao.saveAuthKeyChangeRecord(authKeyChangeRecord)
        }
    }

    val currPassport = MutableLiveData<Passport>()
    val authRecords = currPassport
            .switchMap { it?.let { dao.listAuthRecords(it.address) } ?: MutableLiveData() }

    fun load() {
        val wallet = passportPrefs.wallet.get()
        val password = passportPrefs.password.get()
        val credential = try {
            Wallet.decrypt(password, parseWalletFile(wallet))
        } catch (e: Exception) {
            return // can't load credential
        }

        val keyAlias = passportPrefs.authKeyAlias.get()
        val pub = passportPrefs.authKeyPublicKey.get()?.let { Numeric.hexStringToByteArray(it) }
        val authKey = if (keyAlias != null && pub != null) AuthKey(keyAlias, pub) else null
        currPassport.value = Passport(
                credential = Credentials.create(credential),
                authKey = authKey,
                avatarUri = passportPrefs.avatar.get()?.let { Uri.parse(it) },
                nickname = passportPrefs.nickname.get()
        )
    }

    fun getCurrentPassport(): Passport? {
        return currPassport.value
    }

    val passportEnabled
        get() = currPassport.value?.authKey != null

    val passportExists
        get() = currPassport.value != null

    @WorkerThread
    fun saveNewPassport(credential: Credentials, password: String, authKey: AuthKey?) {
        val walletFile = EthsHelper.makeWalletFile(password, credential.ecKeyPair)
        val passport = Passport(
                credential = credential,
                authKey = authKey,
                avatarUri = null,
                nickname = null
        )
        currPassport.postValue(passport)
        passportPrefs.let {
            it.wallet.set(gson.toJson(walletFile))
            it.password.set(password)
            if (authKey == null) {
                it.authKeyAlias.clear()
                it.authKeyPublicKey.clear()
            } else {
                it.authKeyPublicKey.set(Numeric.toHexString(authKey.publicKeyEncoded))
                it.authKeyAlias.set(authKey.keyAlias)
            }
            it.avatar.clear()
            it.nickname.clear()
        }
    }

    fun removeEntirePassportInformation() {
        currPassport.value = null
        passportPrefs.clearAll()
    }

    fun updatePassportNickname(passport: Passport, newNickname: String?) {
        if (getCurrentPassport()?.address == passport.address) {
            val copy = passport.copy(nickname = newNickname)
            if (newNickname != null) {
                passportPrefs.nickname.set(newNickname)
            } else {
                passportPrefs.nickname.clear()
            }
            currPassport.value = copy
        }
    }

    fun saveAvatar(bitmap: Bitmap): Single<Pair<Passport, Uri>> {
        val passport = getCurrentPassport()!!
        val filesDir = App.get().filesDir
        return Single.just(passport to bitmap)
                .map { (passport, bitmap) ->
                    val imgDir = File(filesDir, ChooseCutImageActivity.IMG_DIR)
                    if (!imgDir.exists()) {
                        val mkdirs = imgDir.mkdirs()
                        assert(mkdirs)
                    }
                    val fileName =
                            "${passport.address}-${System.currentTimeMillis().toString(16).padStart(
                                    16,
                                    '0'
                            )}.png"
                    val imgFile = File(filesDir, fileName)
                    imgFile.outputStream()
                            .use {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 87, it)
                            }
                    passport to Uri.fromFile(imgFile)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    updatePassportUserAvatar(it.first, it.second)
                    it
                }
    }

    fun updatePassportUserAvatar(passport: Passport, uri: Uri?) {
        if (getCurrentPassport()?.address == passport.address) {
            val copy = passport.copy(avatarUri = uri)
            if (uri != null) {
                passportPrefs.avatar.set(uri.toString())
            } else {
                passportPrefs.avatar.clear()
            }
            currPassport.value = copy
        }
    }

    fun getPassword(): String {
        return passportPrefs.password.get()!!
    }

    fun getWallet(): String {
        return passportPrefs.wallet.get()!!
    }

    @WorkerThread
    fun changePassword(passport: Passport, newPassword: String): Boolean {
        val walletFile = EthsHelper.makeWalletFile(newPassword, passport.credential.ecKeyPair)
        passportPrefs.wallet.set(gson.toJson(walletFile))
        passportPrefs.password.set(newPassword)
        return true
    }


    private val passportPrefs = PassportPrefs(context.getSharedPreferences(PASSPORT_SP_NAME, Context.MODE_PRIVATE))

    companion object {
        const val PASSPORT_SP_NAME = "user_eths_passport"

        const val PASSPORT_WALLET_FILE = "passport_keystore"
        const val PASSPORT_WALLET_PASSWORD = "passport_password"
        const val USER_NICKNAME = "user_nickname"
        const val USER_AVATAR_URI = "user_avatar_uri"
        const val AUTH_KEY_ALIAS = "auth_key_alias"
        const val AUTH_KEY_PUB = "auth_key_pub"

        @JvmStatic
        val gson = GsonBuilder()
                .registerTypeAdapter(WalletFile::class.java, WalletFileDeserializer())
                .create()

        @JvmStatic
        fun parseWalletFile(wallet: String?) = gson.fromJson(wallet, WalletFile::class.java)
    }

    private class PassportPrefs(sp: SharedPreferences) : Prefs(sp) {
        val wallet = StringPref(PASSPORT_WALLET_FILE)
        val password = StringPref(PASSPORT_WALLET_PASSWORD)
        val avatar = StringPref(USER_AVATAR_URI)
        val nickname = StringPref(USER_NICKNAME)
        val authKeyAlias = StringPref(AUTH_KEY_ALIAS)
        val authKeyPublicKey = StringPref(AUTH_KEY_PUB)
    }
}