package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.text.InputFilter
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
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.*
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustTransferDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustWithdrawCheckPasswdDialog
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.android.localprotect.FingerPrintHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTransferBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.trustpocket.TransferBean
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
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
class TrustTransferActivity : BindActivity<ActivityTrustTransferBinding>() {

    private val mobileUserId get() = intent.getStringExtra("mobileUserId")
    private val memberId get() = intent.getStringExtra("memberId")
    private val photoUrl get() = intent.getStringExtra("photoUrl")
    private val nickName get() = intent.getStringExtra("nickName")
    private val mobile get() = intent.getStringExtra("mobile")
    private val address get() = intent.getStringExtra("address")

    lateinit var keyStore: KeyStore

    lateinit var mFragmentManager: FragmentManager

    private lateinit var mCode: String

    private lateinit var mTotalAccount: String

    private lateinit var trustTransferDialog: TrustTransferDialog

    private lateinit var trustWithdrawCheckPasswdDialog: TrustWithdrawCheckPasswdDialog

    private lateinit var mAccount: String

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        //mCode = intent.getStringExtra("code")

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, TrustChooseCoinActivity::class.java).putExtra("use", "reTransfer"),
                    RequestCodes.CHOOSE_TRANSFER_CODE
            )
        }

        val filters = arrayOf<InputFilter>(EditInputFilter())

        binding.etAccount.filters = filters

        binding.url = photoUrl
        binding.nickName = "昵称:\n$nickName"
        binding.mobile = mobile
        binding.address = address

        binding.btTransfer.onClick {
            val code = binding.tvName.text
            mAccount = binding.etAccount.text.trim().toString()

            if ("请选择" == code) {
                toast("请选择币种")
            } else if ("" == mAccount) {
                toast("请输入转账数量")
            } else if (mAccount.toBigDecimal().subtract(mTotalAccount.toBigDecimal()) > BigDecimal.ZERO) {
                toast("超过可提总量")
            } else {

                if (isNumberkeep8(mAccount)) {
                    trustTransferDialog = TrustTransferDialog(this)
                    trustTransferDialog.setParameters(mobile, address, "$mAccount $mCode", "$mTotalAccount $mCode")

                    trustTransferDialog.setOnClickListener(object : TrustTransferDialog.OnClickListener {
                        override fun transfer() {


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
                    trustTransferDialog.show()
                } else {
                    toast("最多小数点后面8位")
                }
            }
        }
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
                        transfer(mAccount, mobileUserId)
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

    fun transfer(amount: String, mobileUserId: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.transfer(it, mCode, amount.toBigDecimal(), mobileUserId).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.status == TransferBean.Status.SUCCESS) {
                        trustTransferDialog.dismiss()
                        navigateTo(TrustTransferSuccessActivity::class.java) {
                            putExtra("mobile", mobile)
                            putExtra("address", address)
                            putExtra("account", it.amount.decimalValue + " " + mCode)
                        }
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun getBalance(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .subscribe({
                    mTotalAccount = it.availableAmount.assetValue.amount.toBigDecimal().setSelfScale(8)
                    binding.tvAccount.text = mTotalAccount + " " + it.availableAmount.assetValue.assetCode
                }, {
                    toast(it.message.toString())
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_TRANSFER_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mCode = data!!.getStringExtra("code")
                    binding.tvName.text = mCode
                    getBalance(mCode)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @TargetApi(23)
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

    @TargetApi(23)
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
                transfer(mAccount, mobileUserId)
            }
        })

        fragment.setOnClickListener(object : FingerCheckDialog.OnClickListener {
            override fun cancel() {
                val getRedpacketDialog = GetRedpacketDialog(this@TrustTransferActivity)
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
