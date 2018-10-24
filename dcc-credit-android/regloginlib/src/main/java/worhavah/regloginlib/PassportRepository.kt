package worhavah.regloginlib


import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.google.gson.GsonBuilder
import io.wexchain.android.common.Prefs
import io.wexchain.android.common.kotlin.weak
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.crypto.WalletFileDeserializer
import org.web3j.utils.Numeric

/**
 *
 */
object PassportRepository {

    private const val PASSPORT_SP_NAME = "user_eths_passport"

    private const val PASSPORT_WALLET_FILE = "passport_keystore"
    private const val PASSPORT_WALLET_PASSWORD = "passport_password"
    private const val USER_NICKNAME = "user_nickname"
    private const val USER_AVATAR_URI = "user_avatar_uri"
    private const val AUTH_KEY_ALIAS = "auth_key_alias"
    private const val AUTH_KEY_PUB = "auth_key_pub"
    private const val SCF_ACCOUNT_EXISTS = "scf_account_exists"

    private const val DEFAULT_BENEFICIARY_ADDRESS = "default_beneficiary_address"
    var context: Context? by weak()
    //lateinit var  dao: PassportDao
    lateinit private var passportPrefs: PassportPrefs

    // lateinit private var beneficiaryAddresses: LiveData<List<BeneficiaryAddress>>
    //init
    @Synchronized
    fun letinit(context: Context): PassportRepository {
        if (null == this.context) {
            this.context = context
            doinit(context)
        }
        return PassportRepository
    }

    private fun doinit(context: Context) {
        passportPrefs = PassportPrefs(PassportRepository.context!!.getSharedPreferences(PASSPORT_SP_NAME, Context.MODE_PRIVATE))
    }

    val currPassport = MutableLiveData<Passport>()

    private val selectedBeneficiaryAddress = MutableLiveData<String>()

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

    @WorkerThread
    fun saveNewPassport(credential: Credentials, password: String, authKey: AuthKey?): Passport {
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
        return passport
    }

    @WorkerThread
    fun saveNewPassR(credential: Credentials, authKey: AuthKey?): Passport {
        //  val walletFile = EthsHelper.makeWalletFile(password, credential.ecKeyPair)
        val passport = Passport(
                credential = credential,
                authKey = authKey,
                avatarUri = null,
                nickname = null
        )
        currPassport.postValue(passport)
        passportPrefs.let {
            //  it.wallet.set(gson.toJson(walletFile))
            //  it.password.set(password)
            it.saveAuthKey(authKey)
            it.avatar.clear()
            it.nickname.clear()
        }
        return passport
    }

    @MainThread
    fun updateAuthKey(passport: Passport, authKey: AuthKey?) {
        if (getCurrentPassport()?.address == passport.address) {
            passportPrefs.saveAuthKey(authKey)
            val copy = passport.copy(authKey = authKey)
            currPassport.value = copy
        }
    }

    @JvmStatic
    val gson = GsonBuilder()
            .registerTypeAdapter(WalletFile::class.java, WalletFileDeserializer())
            .create()

    @JvmStatic
    fun parseWalletFile(wallet: String?) = gson.fromJson(wallet, WalletFile::class.java)


    class PassportPrefs(sp: SharedPreferences) : Prefs(sp) {
        val wallet = StringPref(PASSPORT_WALLET_FILE)
        val password = StringPref(PASSPORT_WALLET_PASSWORD)
        val avatar = StringPref(USER_AVATAR_URI)
        val nickname = StringPref(USER_NICKNAME)
        val authKeyAlias = StringPref(AUTH_KEY_ALIAS)
        val authKeyPublicKey = StringPref(AUTH_KEY_PUB)
        val defaultBeneficiaryAddress = StringPref(DEFAULT_BENEFICIARY_ADDRESS)
        val scfAccountExists = BoolPref(SCF_ACCOUNT_EXISTS, false)

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
