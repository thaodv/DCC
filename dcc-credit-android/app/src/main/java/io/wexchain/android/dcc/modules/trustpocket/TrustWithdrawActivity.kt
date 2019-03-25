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
import android.widget.Toast
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
import io.wexchain.android.localprotect.FingerPrintHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustWithdrawBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
import io.wexchain.dccchainservice.domain.trustpocket.WithdrawBean
import io.wexchain.digitalwallet.util.isNumberkeep8
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RequiresApi(Build.VERSION_CODES.M)
class TrustWithdrawActivity : BindActivity<ActivityTrustWithdrawBinding>(), TextWatcher {

    override val contentLayoutId: Int get() = R.layout.activity_trust_withdraw

    lateinit var keyStore: KeyStore

    lateinit var mFragmentManager: FragmentManager


    private var mCode: String? = null
    private var mUrl: String? = null
    private var mAddress: String? = null
    private lateinit var mAccount: String

    private lateinit var mMinAccount: String

    private lateinit var mTotalAccount: String
    private lateinit var mFee: String
    private lateinit var mToAccount: String

    private lateinit var trustWithdrawDialog: TrustWithdrawDialog

    private lateinit var trustWithdrawCheckPasswdDialog: TrustWithdrawCheckPasswdDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mCode = intent.getStringExtra("code")
        mUrl = intent.getStringExtra("url")
        mAddress = intent.getStringExtra("address")

        if (null != mAddress) {
            binding.etAddress.setText(mAddress)
            binding.etAddress.setSelection(mAddress!!.length)
        }

        if (null != mCode && null != mUrl) {
            getAssetConfigMinAmountByCode(mCode!!)

            getBalance(mCode!!)

            binding.url = mUrl
            binding.name = mCode

            binding.tvAssetCode.text = mCode
        }

        val filters = arrayOf<InputFilter>(EditInputFilter())

        binding.etAccount.filters = filters

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
                toast("提币地址不能为空")
            } else {
                binding.etAccount.setText(mTotalAccount)
            }
        }

        binding.btWithdraw.onClick {
            mAddress = binding.etAddress.text.trim().toString()
            mAccount = binding.etAccount.text.trim().toString()

            if (null != mAddress && "" == mAddress) {
                toast("提币地址不能为空")
            } else if ("" == mAccount) {
                toast("提币数量不能为空")
            } else if (mAccount.toBigDecimal().subtract(mMinAccount.toBigDecimal()) < BigDecimal.ZERO) {
                toast("最低" + mMinAccount + "个起提")
            } else if (mAccount.toBigDecimal().subtract(mTotalAccount.toBigDecimal()) > BigDecimal.ZERO) {
                toast("超过可提总量")
            } else {

                if (isNumberkeep8(mAccount)) {
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
                    toast("最多小数点后面8位")
                }
            }
        }

        binding.etAccount.addTextChangedListener(this)
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
                            putExtra("account", it.amount.decimalValue.toBigDecimal().setScale(8, RoundingMode.DOWN).toPlainString() + " " + it.assetCode)
                        }
                        finish()
                        trustWithdrawDialog.dismiss()

                    } else {
                        toast("系统错误")
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s.toString().trim()
        if (null == mCode) {
            toast("请先选择币种")
        } else {
            if ("" != text) {
                getWithdrawFee(mCode!!, binding.etAddress.text.trim().toString(), text)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getWithdrawFee(code: String, address: String, amount: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getWithdrawFee(it, code, address, amount).check()
                }
                .doMain()
                .subscribe({
                    mFee = it.decimalValue.toBigDecimal().setScale(8, RoundingMode.DOWN).toPlainString()

                    binding.tvFee.text = "手续费$mFee $mCode"

                    mToAccount = amount.toBigDecimal().setScale(8, RoundingMode.DOWN).toPlainString()

                    binding.tvToAccount.text = mToAccount

                }, {
                    //toast(it.message.toString())
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
                    mTotalAccount = it.availableAmount.assetValue.amount.toBigDecimal().setScale(8, RoundingMode.DOWN).toPlainString()
                    binding.tvAccount.text = mTotalAccount + " " + it.availableAmount.assetValue.assetCode
                }, {
                    toast(it.message.toString())
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
                    binding.etAccount.hint = "最低" + it + "个起提"
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
                        deleteDialog.mTvText.text = "密码输入错误，超过3次将被锁定3小时，您还有${it.remainValidateTimes}次机会"
                        deleteDialog.mTvText.gravity = Gravity.LEFT
                        deleteDialog.setBtnText("", "确定")
                        deleteDialog.mBtSure.visibility = View.GONE
                        deleteDialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
                            override fun cancel() {}

                            override fun sure() {

                            }
                        })
                        deleteDialog.show()
                    } else if (it.result == ValidatePaymentPasswordBean.Status.LOCKED) {
                        val deleteDialog = DeleteAddressBookDialog(this)
                        deleteDialog.mTvText.text = "您的密码已被暂时锁定，请等待解锁"
                        deleteDialog.mTvText.gravity = Gravity.LEFT
                        deleteDialog.setBtnText("", "确定")
                        deleteDialog.mBtSure.visibility = View.GONE
                        deleteDialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
                            override fun cancel() {}

                            override fun sure() {

                            }
                        })
                        deleteDialog.show()
                    } else {
                        toast("系统错误")
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_WITHDRAW_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mCode = data!!.getStringExtra("code")
                    mUrl = data!!.getStringExtra("url")

                    binding.url = mUrl
                    binding.name = mCode

                    binding.tvAssetCode.text = mCode

                    getAssetConfigMinAmountByCode(mCode!!)

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
            Toast.makeText(this, "您的系统版本过低，不支持指纹功能", Toast.LENGTH_SHORT).show()
            return false
        } else {
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            val fingerprintManager = getSystemService(FingerprintManager::class.java)
            if (!fingerprintManager!!.isHardwareDetected) {
                Toast.makeText(this, "您的手机不支持指纹功能", Toast.LENGTH_SHORT).show()
                return false
            } else if (!keyguardManager!!.isKeyguardSecure) {
                Toast.makeText(this, "您还未设置锁屏，请先设置锁屏并添加一个指纹", Toast.LENGTH_SHORT).show()
                return false
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(this, "您至少需要在系统设置中添加一个指纹", Toast.LENGTH_SHORT).show()
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
                getRedpacketDialog.setTitle("提示")
                getRedpacketDialog.setIbtCloseVisble(View.GONE)
                getRedpacketDialog.setText("确定要退出吗？")
                getRedpacketDialog.setBtnText("输入密码", "退出")
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


