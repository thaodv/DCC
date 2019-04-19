package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.common.tools.EditInputFilter
import io.wexchain.android.common.tools.rsa.EncryptUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.other.QrScannerActivity
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.view.dialog.FingerCheckDialog
import io.wexchain.android.dcc.view.dialog.GetRedpacketDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustWithdrawCheckPasswdDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustWithdrawDialog
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.android.localprotect.FingerPrintHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustWithdrawBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
import io.wexchain.dccchainservice.domain.trustpocket.WithdrawBean
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.M)
class TrustWithdrawActivity : BindActivity<ActivityTrustWithdrawBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_withdraw

    lateinit var keyStore: KeyStore

    lateinit var mFragmentManager: FragmentManager


    private var mUse: String = "1"
    private var mCode: String? = null
    private var mUrl: String? = null
    private var mAddress: String? = null
    private lateinit var mAccount: String

    private lateinit var mMinAccount: String

    private var mTotalAccount: String = BigDecimal.ZERO.toPlainString()
    private lateinit var mFee: String
    private var mToAccount: String = BigDecimal.ZERO.toPlainString()

    private lateinit var trustWithdrawDialog: TrustWithdrawDialog

    private lateinit var trustWithdrawCheckPasswdDialog: TrustWithdrawCheckPasswdDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mCode = intent.getStringExtra("code")
        mUrl = intent.getStringExtra("url")
        mAddress = intent.getStringExtra("address")
        mUse = intent.getStringExtra("use")

        if ("2" == mUse) {
            binding.ivImg.visibility = View.INVISIBLE
            binding.name = getString(R.string.please_select)
        }

        if (null != mAddress) {
            binding.etAddress.setText(mAddress)
            binding.etAddress.setSelection(mAddress!!.length)
        }

        if (null != mCode && null != mUrl) {
            getAssetConfigMinAmountByCode(mCode!!)
            getAssetConfig(mCode!!)
            getBalance(mCode!!)

            binding.url = mUrl
            binding.name = mCode

            binding.tvAssetCode.text = mCode

            binding.tvFee.text = getString(R.string.across_trans_poundage) + "0 $mCode"
        }

        /*val filters = arrayOf<InputFilter>(EditInputFilter())

        binding.etAccount.filters = filters*/

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, TrustChooseCoinActivity::class.java).putExtra("use", "reWithdraw"),
                    RequestCodes.CHOOSE_WITHDRAW_CODE
            )
        }

        binding.ivScan.onClick {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_UNSPECIFIED)
            }, RequestCodes.SCAN)
        }

        binding.ivShoose.onClick {
            startActivityForResult(
                    Intent(this, AddressBookActivity::class.java).putExtra("usage", 1),
                    RequestCodes.CHOOSE_BENEFICIARY_ADDRESS
            )
        }

        binding.tvAll.onClick {
            val address = binding.etAddress.text.trim().toString()
            if ("" == address) {
                toast(getString(R.string.trust_pocket_withdraw_tip1))
            } else {
                getWithdrawFee2(mCode!!, address, mTotalAccount)
            }
        }

        binding.btWithdraw.onClick {
            mAddress = binding.etAddress.text.trim().toString()
            mAccount = binding.etAccount.text.trim().toString()

            if (binding.tvName.text.toString() == getString(R.string.please_select)) {
                toast(getString(R.string.please_choose_coin_type))
            } else if (null != mAddress && "" == mAddress) {
                toast(getString(R.string.trust_pocket_withdraw_tip1))
            } else if ("" == mAccount) {
                toast(getString(R.string.trust_pocket_withdraw_tip2))
            } else if (mAccount.toBigDecimal().subtract(mMinAccount.toBigDecimal()) < BigDecimal.ZERO) {
                toast(getString(R.string.minimum) + mMinAccount + getString(R.string.trust_pocket_withdraw_tip3))
            } else if (mAccount.toBigDecimal().subtract(mTotalAccount.toBigDecimal()) > BigDecimal.ZERO) {
                toast(getString(R.string.trust_pocket_transfer_text7))
            } else if (mTotalAccount.toBigDecimal().subtract(mAccount.toBigDecimal().plus(mFee.toBigDecimal())) < BigDecimal.ZERO) {
                toast(getString(R.string.trust_pocket_transfer_text9))
            } else {

                GardenOperations
                        .refreshToken {
                            App.get().marketingApi.validateAddress(it, mAddress!!, mCode!!).check()
                        }
                        .doMain()
                        .subscribe({
                            if (it) {
                                trustWithdrawDialog = TrustWithdrawDialog(this)

                                trustWithdrawDialog.setParameters(mAddress, mAccount + "" + mCode, "$mFee $mCode", "$mToAccount $mCode", "$mTotalAccount $mCode")

                                trustWithdrawDialog.setOnClickListener(object : TrustWithdrawDialog.OnClickListener {
                                    override fun sure() {

                                        val fingerPayStatus = ShareUtils.getBoolean(Extras.SP_TRUST_FINGER_PAY_STATUS, false)

                                        if (fingerPayStatus) {
                                            if (supportFingerprint()) {
                                                initKey()
                                                initCipher()
                                            } else {
                                                checkPasswd()
                                            }
                                        } else {
                                            checkPasswd()
                                        }
                                    }
                                })
                                trustWithdrawDialog.show()
                            } else {
                                toast(getString(R.string.trust_pocket_transfer_text10))
                            }
                        }, {
                            toast(it.message ?: getString(R.string.system_error))
                        })
            }
        }

        binding.etAccount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (null == mCode) {
                    toast(getString(R.string.trust_pocket_withdraw_tip4))
                } else {
                    if ("" != text) {
                        getWithdrawFee(mCode!!, binding.etAddress.text.trim().toString(), text)
                    } else {
                        binding.tvFee.text = getString(R.string.across_trans_poundage) + "0 $mCode"
                        binding.tvToAccount.text = "0"
                    }
                }
            }
        })

        binding.etAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (null == mCode) {
                    toast(getString(R.string.trust_pocket_withdraw_tip4))
                } else {
                    if ("" != text) {
                        getWithdrawFee(mCode!!, text, binding.etAccount.text.trim().toString())
                    } else {
                        binding.tvFee.text = getString(R.string.across_trans_poundage) + "0 $mCode"
                        binding.tvToAccount.text = "0"
                    }
                }
            }
        })
    }

    private fun checkPasswd() {
        trustWithdrawCheckPasswdDialog = TrustWithdrawCheckPasswdDialog(this)

        trustWithdrawCheckPasswdDialog.setOnClickListener(object : TrustWithdrawCheckPasswdDialog.OnClickListener {
            override fun forget() {
                navigateTo(TrustPocketModifyPhoneActivity::class.java) {
                    putExtra("source", "forget")
                }
            }
        })
        trustWithdrawCheckPasswdDialog.show()

        trustWithdrawCheckPasswdDialog.mPassword.setPwdChangeListener(object : PassWordLayout.pwdChangeListener {
            override fun onChange(pwd: String) {
                Log.e("onChange:", pwd)
            }

            override fun onNull() {

            }

            override fun onFinished(pwd: String) {
                Log.e("onFinished:", pwd)
                if (pwd.length == 6) {
                    createPayPwdSecurityContext(pwd)
                }
            }
        })
    }

    private fun withdraw(address: String, account: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.withdraw(it, mCode!!, account.toBigDecimal(), address).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.status == WithdrawBean.Status.PROCESSING || it.status == WithdrawBean.Status.PROCESSING) {

                        navigateTo(TrustWithdrawSuccessActivity::class.java) {
                            putExtra("address", it.receiverAddress)
                            putExtra("account", it.amount.decimalValue.toBigDecimal().setSelfScale(8) + " " + it.assetCode)
                        }
                        finish()
                        trustWithdrawDialog.dismiss()

                    } else {
                        toast(getString(R.string.system_error))
                    }
                }, {
                    toast(it.message ?: getString(R.string.system_error))
                })
    }


    private fun getWithdrawFee(code: String, address: String, amount: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getWithdrawFee(it, code, address, amount).check()
                }
                .doMain()
                .subscribe({
                    mFee = it.decimalValue.toBigDecimal().setSelfScale(8)

                    binding.tvFee.text = getString(R.string.across_trans_poundage) + "$mFee $mCode"

                    mToAccount = amount.toBigDecimal().setSelfScale(8)

                    binding.tvToAccount.text = mToAccount

                }, {
                    //toast(it.message.toString())
                })
    }

    private fun getWithdrawFee2(code: String, address: String, amount: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getWithdrawFee(it, code, address, amount).check()
                }
                .doMain()
                .subscribe({
                    val res = mTotalAccount.toBigDecimal().subtract(it.decimalValue.toBigDecimal()).setScale(8, RoundingMode.DOWN)

                    var result: String = BigDecimal.ZERO.toPlainString()
                    var result2: String = BigDecimal.ZERO.toPlainString()
                    if (res > BigDecimal.ZERO) {
                        result = res.toPlainString()
                        result2 = res.toPlainString()
                    } else {
                        result = mTotalAccount.toBigDecimal().toPlainString()
                        result2 = BigDecimal.ZERO.toPlainString()
                    }

                    binding.etAccount.setText(result)

                    mFee = it.decimalValue.toBigDecimal().setSelfScale(8)

                    binding.tvFee.text = getString(R.string.across_trans_poundage) + "$mFee $mCode"

                    mToAccount = result2

                    binding.tvToAccount.text = mToAccount

                }, {
                    binding.tvFee.text = getString(R.string.across_trans_poundage) + "0 $mCode"
                    binding.etAccount.setText(BigDecimal.ZERO.toPlainString())
                    binding.tvToAccount.text = "0"
                })
    }

    private fun getBalance(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    mTotalAccount = it.availableAmount.assetValue.amount.toBigDecimal().setSelfScale(8)
                    binding.tvAccount.text = mTotalAccount + " " + it.availableAmount.assetValue.assetCode
                }, {
                    if ("getBalance.arg0.mobileUserId:must not be null" == it.message.toString()) {
                        navigateTo(TrustPocketOpenTipActivity::class.java)
                        finish()
                    } else {
                        toast(it.message.toString())
                    }
                })
    }

    private fun getAssetConfigMinAmountByCode(code: String) {

        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getAssetConfigMinAmountByCode(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    mMinAccount = it
                    binding.etAccount.hint = getString(R.string.minimum) + it + getString(R.string.trust_pocket_withdraw_tip3)
                }, {
                    toast(it.message.toString())
                })

    }

    private fun getAssetConfig(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getAssetConfig(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    val filters = arrayOf<InputFilter>(EditInputFilter(it.digit))
                    binding.etAccount.filters = filters
                }, {
                    toast(it.message.toString())
                })
    }

    private fun createPayPwdSecurityContext(pwd: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.createPayPwdSecurityContext(it)
                }
                .doMain()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        prepareInputPwd(pwd)
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun prepareInputPwd(pwd: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.prepareInputPwd(it).check()
                }
                .doMain()
                .subscribe({
                    validatePaymentPassword(it.pubKey, it.salt, pwd)
                }, {
                    toast(it.message.toString())
                })
    }

    private fun validatePaymentPassword(pubKey: String, salt: String, pwd: String) {

        val enpwd = EncryptUtils.getInstance().encode(BigInteger(MessageDigest.getInstance(ScfOperations.DIGEST).digest(pwd.toByteArray(Charsets.UTF_8))).toString(16) + salt, pubKey)
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.validatePaymentPassword(it, enpwd, salt).check()
                }
                .doMain()
                .subscribe({
                    if (it.result == ValidatePaymentPasswordBean.Status.PASSED) {
                        trustWithdrawCheckPasswdDialog.dismiss()
                        withdraw(mAddress!!, mAccount)
                    } else if (it.result == ValidatePaymentPasswordBean.Status.REJECTED) {
                        val deleteDialog = DeleteAddressBookDialog(this)
                        deleteDialog.mTvText.text = getString(R.string.trust_passwd_text5) + it.remainValidateTimes + getString(R.string.trust_passwd_text6)
                        deleteDialog.mTvText.gravity = Gravity.LEFT
                        deleteDialog.setBtnText("", getString(R.string.confirm))
                        deleteDialog.mBtSure.visibility = View.GONE
                        deleteDialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
                            override fun cancel() {}

                            override fun sure() {

                            }
                        })
                        deleteDialog.show()
                    } else if (it.result == ValidatePaymentPasswordBean.Status.LOCKED) {
                        val deleteDialog = DeleteAddressBookDialog(this)
                        deleteDialog.mTvText.text = getString(R.string.trust_passwd_text1)
                        deleteDialog.mTvText.gravity = Gravity.LEFT
                        deleteDialog.setBtnText("", getString(R.string.confirm))
                        deleteDialog.mBtSure.visibility = View.GONE
                        deleteDialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
                            override fun cancel() {}

                            override fun sure() {

                            }
                        })
                        deleteDialog.show()
                    } else {
                        toast(getString(R.string.system_error))
                    }
                }, {
                    toast(it.message ?: getString(R.string.system_error))
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_WITHDRAW_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {

                    if ("2" == mUse) {
                        binding.ivImg.visibility = View.VISIBLE
                    }

                    mCode = data!!.getStringExtra("code")
                    mUrl = data!!.getStringExtra("url")

                    binding.url = mUrl
                    binding.name = mCode

                    binding.tvAssetCode.text = mCode

                    getAssetConfigMinAmountByCode(mCode!!)
                    getAssetConfig(mCode!!)
                    getBalance(mCode!!)

                    val account = binding.etAccount.text.trim().toString()

                    if ("" != account) {
                        getWithdrawFee(mCode!!, binding.etAddress.text.trim().toString(), account)
                    }
                }
            }
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null) {
                    binding.etAddress.setText(address)
                    binding.executePendingBindings()
                    binding.etAddress.setSelection(address.length)
                }
            }
            RequestCodes.CHOOSE_BENEFICIARY_ADDRESS -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    val ba = data?.getSerializableExtra(Extras.EXTRA_SELECT_ADDRESS) as? AddressBook
                    if (ba != null) {
                        binding.etAddress.setText(ba.address)
                        binding.executePendingBindings()
                        binding.etAddress.setSelection(ba.address.length)
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore")
            val builder = KeyGenParameterSpec.Builder(FingerPrintHelper.KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun supportFingerprint(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            toast(R.string.toast_msg2)
            return false
        } else {
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            val fingerprintManager = getSystemService(FingerprintManager::class.java)
            if (!fingerprintManager!!.isHardwareDetected) {
                toast(R.string.toast_msg3)
                return false
            } else if (!keyguardManager!!.isKeyguardSecure) {
                toast(R.string.toast_msg4)
                return false
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                toast(R.string.toast_msg5)
                return false
            }
        }
        return true
    }

    private fun initCipher() {
        try {
            val key = keyStore.getKey(FingerPrintHelper.KEY_NAME, null) as SecretKey
            val cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties
                    .BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            showFingerPrintDialog(cipher)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun showFingerPrintDialog(cipher: Cipher) {

        val fragment = FingerCheckDialog()
        fragment.setCipher(cipher)
        mFragmentManager = supportFragmentManager
        fragment.show(mFragmentManager, "")

        fragment.setOnCallBack(object : FingerCheckDialog.onCallBack {
            override fun onAuthenticated() {
                withdraw(mAddress!!, mAccount)
            }
        })

        fragment.setOnClickListener(object : FingerCheckDialog.OnClickListener {
            override fun cancel() {
                val getRedpacketDialog = GetRedpacketDialog(this@TrustWithdrawActivity)
                getRedpacketDialog.setTitle(getString(R.string.tips))
                getRedpacketDialog.setIbtCloseVisble(View.GONE)
                getRedpacketDialog.setText(getString(R.string.trust_passwd_text2))
                getRedpacketDialog.setBtnText(getString(R.string.trust_passwd_text3), getString(R.string.trust_passwd_text4))
                getRedpacketDialog.setOnClickListener(object : GetRedpacketDialog.OnClickListener {
                    override fun cancel() {
                        checkPasswd()
                    }

                    override fun sure() {

                    }
                })
                getRedpacketDialog.show()
            }
        })
    }
}


