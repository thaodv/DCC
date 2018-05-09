package io.wexchain.android.dcc.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Prefs
import io.wexchain.android.common.distinct
import io.wexchain.android.common.map
import io.wexchain.android.common.switchMap
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.ChooseCutImageActivity
import io.wexchain.android.dcc.chain.EthsHelper
import io.wexchain.android.dcc.domain.AuthKey
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
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
class PassportRepository(
    context: Context,
    val dao: PassportDao
) {

    private val passportPrefs = PassportPrefs(context.getSharedPreferences(PASSPORT_SP_NAME, Context.MODE_PRIVATE))

    val currPassport = MutableLiveData<Passport>()

    val currAddress = currPassport.map { it?.address }.distinct()

    val authRecords = currPassport
        .switchMap { it?.let { dao.listAuthRecords(it.address) } ?: MutableLiveData() }

    val authKeyChangeRecords: LiveData<List<AuthKeyChangeRecord>> = currPassport
        .switchMap { it?.let { dao.listAuthKeyChangeRecords(it.address) } ?: MutableLiveData() }

    val beneficiaryAddresses: LiveData<List<BeneficiaryAddress>> = MediatorLiveData<List<BeneficiaryAddress>>().apply {
        var list: List<BeneficiaryAddress>? = null
        var walletAddr: String? = null
        addSource(dao.listBeneficiaryAddresses()) {
            list = it
            val addr = walletAddr
            val l = list ?: emptyList()
            val composite = if (addr == null) {
                l
            } else {
                listOf(BeneficiaryAddress(addr, WALLET_ADDR_SHORT_NAME)) + l
            }
            postValue(composite)
        }
        addSource(currAddress) {
            walletAddr = it
            val addr = walletAddr
            val l = list ?: emptyList()
            val composite = if (addr == null) {
                l
            } else {
                listOf(BeneficiaryAddress(addr, WALLET_ADDR_SHORT_NAME)) + l
            }
            postValue(composite)
        }
    }

    private val selectedBeneficiaryAddress = MutableLiveData<String>()

    val defaultBeneficiaryAddress: LiveData<String?> = MediatorLiveData<String?>().apply {
        var walletAddr: String? = null
        var selected: String? = null
        addSource(selectedBeneficiaryAddress) {
            selected = it
            postValue(selected ?: walletAddr)
        }
        addSource(currAddress) {
            walletAddr = it
            postValue(selected ?: walletAddr)
        }
    }

    fun getDefaultBeneficiaryAddress(): Single<BeneficiaryAddress> {
        val defaultAddr = selectedBeneficiaryAddress.value
        return if(defaultAddr == null){
            val walletAddr = getCurrentPassport()?.address
            if(walletAddr == null){
                Single.error<BeneficiaryAddress>(IllegalStateException())
            }else{
                Single.just(BeneficiaryAddress(walletAddr, WALLET_ADDR_SHORT_NAME))
            }
        }else{
            return Single.just(defaultAddr)
                .observeOn(RoomHelper.roomScheduler)
                .map {
                    dao.getBeneficiaryAddresseByAddress(defaultAddr)
                        .first()
                }
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun addAuthKeyChangedRecord(authKeyChangeRecord: AuthKeyChangeRecord) {
        RoomHelper.onRoomIoThread {
            dao.saveAuthKeyChangeRecord(authKeyChangeRecord)
        }
    }

    @MainThread
    fun setDefaultBeneficiaryAddress(address: String?) {
        if (address != null && address != getCurrentPassport()?.address) {
            passportPrefs.defaultBeneficiaryAddress.set(address)
            selectedBeneficiaryAddress.value = address
        } else {
            passportPrefs.defaultBeneficiaryAddress.clear()
            selectedBeneficiaryAddress.value = null
        }
    }

    fun load() {
        val wallet = passportPrefs.wallet.get()
        val password = passportPrefs.password.get()
        val credential = try {
            Wallet.decrypt(password, parseWalletFile(wallet))
        } catch (e: Exception) {
            return // can't load credential
        }

        val authKey = passportPrefs.loadAuthKey()
        currPassport.value = Passport(
            credential = Credentials.create(credential),
            authKey = authKey,
            avatarUri = passportPrefs.avatar.get()?.let { Uri.parse(it) },
            nickname = passportPrefs.nickname.get()
        )
        selectedBeneficiaryAddress.value = passportPrefs.defaultBeneficiaryAddress.get()
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
            it.saveAuthKey(authKey)
            it.avatar.clear()
            it.nickname.clear()
        }
    }

    fun removeEntirePassportInformation() {
        currPassport.value = null
        passportPrefs.clearAll()
        selectedBeneficiaryAddress.value = null
        RoomHelper.onRoomIoThread {
            dao.deleteAuthRecords()
            dao.deleteAuthKeyChangeRecords()
            dao.deleteBeneficiaryAddresses()
        }
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
            .doOnSuccess {
                updatePassportUserAvatar(it.first, it.second)
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

    @MainThread
    fun updateAuthKey(passport: Passport, authKey: AuthKey?) {
        if (getCurrentPassport()?.address == passport.address) {
            passportPrefs.saveAuthKey(authKey)
            val copy = passport.copy(authKey = authKey)
            currPassport.value = copy
        }
    }

    fun addBeneficiaryAddress(beneficiaryAddress: BeneficiaryAddress) {
        RoomHelper.onRoomIoThread {
            dao.addOrUpdateBeneficiaryAddress(beneficiaryAddress)
        }
    }

    fun removeBeneficiaryAddress(beneficiaryAddress: BeneficiaryAddress) {
        RoomHelper.onRoomIoThread {
            dao.removeBeneficiaryAddress(beneficiaryAddress)
        }
        if (selectedBeneficiaryAddress.value == beneficiaryAddress.address) {
            selectedBeneficiaryAddress.value = null
        }
    }

    companion object {
        const val WALLET_ADDR_SHORT_NAME = "本地钱包"

        private const val PASSPORT_SP_NAME = "user_eths_passport"

        private const val PASSPORT_WALLET_FILE = "passport_keystore"
        private const val PASSPORT_WALLET_PASSWORD = "passport_password"
        private const val USER_NICKNAME = "user_nickname"
        private const val USER_AVATAR_URI = "user_avatar_uri"
        private const val AUTH_KEY_ALIAS = "auth_key_alias"
        private const val AUTH_KEY_PUB = "auth_key_pub"

        private const val DEFAULT_BENEFICIARY_ADDRESS = "default_beneficiary_address"

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
        val defaultBeneficiaryAddress = StringPref(DEFAULT_BENEFICIARY_ADDRESS)

        fun loadAuthKey(): AuthKey? {
            val keyAlias = authKeyAlias.get()
            val pub = authKeyPublicKey.get()?.let { Numeric.hexStringToByteArray(it) }
            val authKey = if (keyAlias != null && pub != null) AuthKey(keyAlias, pub) else null
            return authKey
        }

        fun saveAuthKey(authKey: AuthKey?) {
            if (authKey == null) {
                authKeyAlias.clear()
                authKeyPublicKey.clear()
            } else {
                authKeyPublicKey.set(Numeric.toHexString(authKey.publicKeyEncoded))
                authKeyAlias.set(authKey.keyAlias)
            }
        }

    }
}