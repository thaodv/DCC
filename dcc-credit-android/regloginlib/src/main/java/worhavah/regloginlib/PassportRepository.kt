package worhavah.regloginlib


import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.annotation.MainThread
import com.google.gson.GsonBuilder
import io.wexchain.android.common.Prefs
import io.wexchain.android.common.kotlin.weak
import io.wexchain.android.common.tools.AESSign
import io.wexchain.android.common.tools.CommonUtils
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
        var wallet = passportPrefs.wallet.get()

        if (null == wallet) {
            wallet = ""
        } else {
            wallet = AESSign.decryptPsw(wallet, CommonUtils.getMacAddress())
        }


        var password = passportPrefs.password.get()

        if (null == password) {
            password = ""
        } else {
            password = AESSign.decryptPsw(password, CommonUtils.getMacAddress())
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
