package io.wexchain.android.dcc.repo

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.annotation.WorkerThread
import com.google.gson.GsonBuilder
import io.wexchain.android.common.Prefs
import io.wexchain.android.dcc.chain.EthsHelper
import io.wexchain.android.dcc.domain.AuthKey
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.crypto.WalletFileDeserializer
import org.web3j.utils.Numeric

/**
 *
 */
class PassportRepository(context: Context) {


    fun addAuthKeyChangedRecord(authKeyChangeRecord: AuthKeyChangeRecord) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val currPassport = MutableLiveData<Passport>()

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

    fun getCurrentPassport():Passport?{
        return currPassport.value
    }

    val passportEnabled
        get() = currPassport.value?.authKey != null

    val passportExists
        get() = currPassport.value != null

    @WorkerThread
    fun savePassport(credential: Credentials, password: String, authKey: AuthKey?) {
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