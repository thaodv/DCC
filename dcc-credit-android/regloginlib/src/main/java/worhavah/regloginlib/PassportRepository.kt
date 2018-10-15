package worhavah.regloginlib

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
import io.wexchain.android.common.kotlin.weak
import io.wexchain.android.common.map


import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.crypto.WalletFileDeserializer
import org.web3j.utils.Numeric
import java.io.File

/**
 *
 */
object PassportRepository {
    const val WALLET_ADDR_SHORT_NAME = "本地钱包"

    private const val PASSPORT_SP_NAME = "user_eths_passport"

    private const val PASSPORT_WALLET_FILE = "passport_keystore"
    private const val PASSPORT_WALLET_PASSWORD = "passport_password"
    private const val USER_NICKNAME = "user_nickname"
    private const val USER_AVATAR_URI = "user_avatar_uri"
    private const val AUTH_KEY_ALIAS = "auth_key_alias"
    private const val AUTH_KEY_PUB = "auth_key_pub"
    private const val SCF_ACCOUNT_EXISTS = "scf_account_exists"

    private const val DEFAULT_BENEFICIARY_ADDRESS = "default_beneficiary_address"
    var  context: Context? by weak()
    //lateinit var  dao: PassportDao
    lateinit private  var passportPrefs:PassportPrefs
   // lateinit private var beneficiaryAddresses: LiveData<List<BeneficiaryAddress>>
    //init
    @Synchronized
    fun  letinit(context: Context): PassportRepository {
        if(null == this.context){
            this.context=context
            doinit(context)
        }
        return PassportRepository
    }

    private fun doinit(context: Context) {
       // dao = PassportDatabase.createDatabase(context).dao
        passportPrefs = PassportPrefs(PassportRepository.context!!.getSharedPreferences(PASSPORT_SP_NAME, Context.MODE_PRIVATE))

    }


    val currPassport = MutableLiveData<Passport>()

    val currAddress = currPassport.map { it?.address }.distinct()





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



    @MainThread
    fun setDefaultBeneficiaryAddress(address: String?) {
        if (address != null && address != getCurrentPassport()?.address) {
            passportPrefs.defaultBeneficiaryAddress.set(address)
            selectedBeneficiaryAddress.value = address
        } else {
            passportPrefs.defaultBeneficiaryAddress.clear()
            selectedBeneficiaryAddress.value = null/**/
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
    fun saveNewPassport(credential: Credentials, password: String, authKey: AuthKey?): Passport{
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
    fun saveNewPassR (credential: Credentials,  authKey: AuthKey?): Passport{
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

    fun removeEntirePassportInformation() {
        currPassport.value = null
        passportPrefs.clearAll()
        selectedBeneficiaryAddress.value = null

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
        val filesDir = context!!.filesDir
        return Single.just(passport to bitmap)
            .map { (passport, bitmap) ->
                val imgDir = File(filesDir, "avatar_images")
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



    var scfAccountExists:Boolean
        get() = passportPrefs.scfAccountExists.get()
        set(value) {
            passportPrefs.scfAccountExists.set(value)
        }




        @JvmStatic
        val gson = GsonBuilder()
            .registerTypeAdapter(WalletFile::class.java, WalletFileDeserializer())
            .create()

        @JvmStatic
        fun parseWalletFile(wallet: String?) = gson.fromJson(wallet, WalletFile::class.java)


    public class PassportPrefs(sp: SharedPreferences) : Prefs(sp) {
        val wallet = StringPref(PASSPORT_WALLET_FILE)
        val password = StringPref(PASSPORT_WALLET_PASSWORD)
        val avatar = StringPref(USER_AVATAR_URI)
        val nickname = StringPref(USER_NICKNAME)
        val authKeyAlias = StringPref(AUTH_KEY_ALIAS)
        val authKeyPublicKey = StringPref(AUTH_KEY_PUB)
        val defaultBeneficiaryAddress = StringPref(DEFAULT_BENEFICIARY_ADDRESS)
        val scfAccountExists = BoolPref(SCF_ACCOUNT_EXISTS,false)

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