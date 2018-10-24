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
import io.wexchain.android.common.tools.AESSign
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.ChooseCutImageActivity
import io.wexchain.android.dcc.chain.EthsHelper
import io.wexchain.android.dcc.domain.AuthKey
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.*
import io.wexchain.android.dcc.tools.*
import io.wexchain.ipfs.utils.doMain
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.crypto.WalletFileDeserializer
import org.web3j.utils.Numeric
import worhavah.regloginlib.Net.Networkutils
import java.io.File

/**
 *
 */
class PassportRepository(
        context: Context,
        val dao: PassportDao
) {

    private val passportPrefs = PassportPrefs(context.getSharedPreferences(PASSPORT_SP_NAME, Context.MODE_PRIVATE))
    private val passportPrefs2 = worhavah.regloginlib.PassportRepository.PassportPrefs(context.getSharedPreferences(PASSPORT_SP_NAME, Context.MODE_PRIVATE))
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

    val addressBooks: LiveData<List<AddressBook>> = MediatorLiveData<List<AddressBook>>().apply {
        addSource(dao.getAllAddressBook()) {
            postValue(it)
        }
    }

    fun getAddressBookByAddress(address: String): LiveData<AddressBook> {
        return MediatorLiveData<AddressBook>().apply {
            addSource(dao.getAddressBookByAddress(address)) {
                postValue(it)
            }
        }
    }

    fun addTransRecord(transRecord: TransRecord) {
        RoomHelper.onRoomIoThread {
            dao.addTransRecord(transRecord)
        }
    }

    fun getTransRecord(): LiveData<List<TransRecord>> {
        return MediatorLiveData<List<TransRecord>>().apply {
            addSource(dao.getTransRecord()) {
                postValue(it)
            }
        }
    }

    fun getTransRecordByAddress(address: String): LiveData<List<TransRecord>> {
        return MediatorLiveData<List<TransRecord>>().apply {
            addSource(dao.getTransRecordByAddress(address)) {
                postValue(it)
            }
        }
    }

    fun updateTransRecord(record: List<TransRecord>) {
        RoomHelper.onRoomIoThread {
            dao.updateTransRecord(record)
        }
    }

    fun queryBookAddress(name: String): LiveData<List<AddressBook>> {
        return MediatorLiveData<List<AddressBook>>().apply {
            addSource(dao.queryAddressBookByShortName(name)) {
                postValue(it)
            }
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
        return if (defaultAddr == null) {
            val walletAddr = getCurrentPassport()?.address
            if (walletAddr == null) {
                Single.error<BeneficiaryAddress>(IllegalStateException())
            } else {
                Single.just(BeneficiaryAddress(walletAddr, WALLET_ADDR_SHORT_NAME))
            }
        } else {
            return Single.just(defaultAddr)
                    .doRoom()
                    .map {
                        dao.getBeneficiaryAddresseByAddress(defaultAddr).first()
                    }
                    .doMain()
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
        var wallet = passportPrefs.wallet.get()

        wallet = if (null == wallet) {
            ""
        } else {
            AESSign.decryptPsw(wallet, CommonUtils.getMacAddress())
        }


        var password = passportPrefs.password.get()

        password = if (null == password) {
            ""
        } else {
            AESSign.decryptPsw(password, CommonUtils.getMacAddress())
        }

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
            it.wallet.set(AESSign.encryptPsw(gson.toJson(walletFile), CommonUtils.getMacAddress()))
            it.password.set(AESSign.encryptPsw(password, CommonUtils.getMacAddress()))
            it.saveAuthKey(authKey)
            it.avatar.clear()
            it.nickname.clear()
        }
        passportPrefs2.let {
            it.wallet.set(AESSign.encryptPsw(gson.toJson(walletFile), CommonUtils.getMacAddress()))
            it.password.set(AESSign.encryptPsw(password, CommonUtils.getMacAddress()))
            it.saveAuthKey(switchAuthkey(authKey))
            it.avatar.clear()
            it.nickname.clear()
        }
    }

    fun switchAuthkey(it: AuthKey?): worhavah.regloginlib.AuthKey {
        return worhavah.regloginlib.AuthKey(it!!.keyAlias, it.publicKeyEncoded)
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
                    val imgFile = File(imgDir, fileName)
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

    fun createIpfsKey(psw: String): ByteArray {
        val address = currPassport.value!!.address
        return (address + psw).toByteArray().toSha256()
    }

    fun createIpfsAESKey(psw: String): ByteArray {
        val privateKey = getCurrentPassport()!!.getPrivateKey()
        return (privateKey + psw).toByteArray().toSha256()
    }

    fun getIpfsKeyHash(isdecrypt: Boolean = true): String? {
        val key = passportPrefs.ipfsKeyHash.get()
        return if (!key.isNullOrEmpty() && isdecrypt) {
            AESSign.decryptPsw(key!!, getEncryptKey())
        } else {
            key
        }
    }

    fun getIpfsAESKey(isdecrypt: Boolean = true): String? {
        val key = passportPrefs.ipfsAesKey.get()
        return if (!key.isNullOrEmpty() && isdecrypt) {
            AESSign.decryptPsw(key!!, getEncryptKey())
        } else {
            key
        }
    }

    fun setIpfsKeyHash(key: String) {
        val encryptKey = AESSign.encryptPsw(key, getEncryptKey())
        passportPrefs.ipfsKeyHash.set(encryptKey)
    }

    fun setIpfsAESKey(key: String) {
        val encryptKey = AESSign.encryptPsw(key, getEncryptKey())
        passportPrefs.ipfsAesKey.set(encryptKey)
    }

    fun getEncryptKey(): String {
        val packageName = CommonUtils.getPackageName()
        val macaddress = CommonUtils.getMacAddress()
        return packageName + macaddress
    }

    fun setIpfsUrlConfig(host: String, port: String) {
        passportPrefs.ipfsUrlHost.set(host)
        passportPrefs.ipfsUrlPort.set(port)
    }

    fun getIpfsUrlConfig(): Pair<String?, String?> {
        val host = passportPrefs.ipfsUrlHost.get()
        val port = passportPrefs.ipfsUrlPort.get()
        return host to port
    }

    fun getIpfsHostStatus(): Boolean {
        return passportPrefs.ipfsHostStatus.get()
    }

    fun setIpfsHostStatus(status: Boolean) {
        passportPrefs.ipfsHostStatus.set(status)
    }

    fun getPassword(): String {
        return passportPrefs.password.get()!!
    }

    fun getDecryptPasswd(): String {
        return AESSign.decryptPsw(passportPrefs.password.get()!!, CommonUtils.getMacAddress())
    }

    fun getWallet(): String {
        return passportPrefs.wallet.get()!!
    }

    fun getDecryptWallet(): String {
        return AESSign.decryptPsw(passportPrefs.wallet.get()!!, CommonUtils.getMacAddress())
    }

    fun setPassword(password: String) {
        return passportPrefs.password.set(AESSign.encryptPsw(password, CommonUtils.getMacAddress()))
    }

    fun setWallet(wallet: String) {
        return passportPrefs.wallet.set(AESSign.encryptPsw(wallet, CommonUtils.getMacAddress()))
    }

    @WorkerThread
    fun changePassword(passport: Passport, newPassword: String): Boolean {
        val walletFile = EthsHelper.makeWalletFile(newPassword, passport.credential.ecKeyPair)
        passportPrefs.wallet.set(AESSign.encryptPsw(gson.toJson(walletFile), CommonUtils.getMacAddress()))
        passportPrefs.password.set(AESSign.encryptPsw(newPassword, CommonUtils.getMacAddress()))
        return true
    }

    @MainThread
    fun updateAuthKey(passport: Passport, authKey: AuthKey?) {
        if (getCurrentPassport()?.address == passport.address) {
            passportPrefs.saveAuthKey(authKey)
            passportPrefs2.saveAuthKey(switchAuthkey(authKey))
            val copy = passport.copy(authKey = authKey)
            currPassport.value = copy
            Networkutils.passportRepository.currPassport.value = Networkutils.passportRepository.currPassport.value!!.copy(authKey = switchAuthkey(authKey))
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

    fun addOrUpdateAddressBook(addressBook: AddressBook) {
        RoomHelper.onRoomIoThread {
            dao.addOrUpdateAddressBook(addressBook)
        }
    }

    fun removeAddressBook(addressBook: AddressBook) {
        RoomHelper.onRoomIoThread {
            dao.removeAddressBook(addressBook)
        }
    }

    var scfAccountExists: Boolean
        get() = passportPrefs.scfAccountExists.get()
        set(value) {
            passportPrefs.scfAccountExists.set(value)
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
        private const val SCF_ACCOUNT_EXISTS = "scf_account_exists"
        private const val IPFS_KEY_HASH = "ipfs_hash_key"
        private const val IPFS_AES_KEY = "ipfs_aes_key"
        private const val IPFS_URL_HOST = "ipfs_url_host"
        private const val IPFS_URL_PORT = "ipfs_url_port"
        private const val IPFS_DEFAULT_HOST_STATUS = "ipfs_default_host_status"

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
        val scfAccountExists = BoolPref(SCF_ACCOUNT_EXISTS, false)
        val ipfsKeyHash = StringPref(IPFS_KEY_HASH)
        val ipfsAesKey = StringPref(IPFS_AES_KEY)
        val ipfsUrlHost = StringPref(IPFS_URL_HOST)
        val ipfsUrlPort = StringPref(IPFS_URL_PORT)
        val ipfsHostStatus = BoolPref(IPFS_DEFAULT_HOST_STATUS, true)

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
