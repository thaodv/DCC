package io.wexchain.android.dcc.modules.other

import android.Manifest
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.tools.rsa.EncryptUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.home.CreatePassportActivity
import io.wexchain.android.dcc.modules.home.HomeActivity
import io.wexchain.android.dcc.modules.passport.PassportImportActivity
import io.wexchain.android.dcc.modules.paymentcode.PaymentSuccessActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketModifyPhoneActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustRechargeActivity
import io.wexchain.android.dcc.tools.*
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.view.dialog.FingerCheckDialog
import io.wexchain.android.dcc.view.dialog.GetRedpacketDialog
import io.wexchain.android.dcc.view.dialog.paymentcode.PaymentCodePayDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustWithdrawCheckPasswdDialog
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.android.localprotect.FingerPrintHelper
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
import io.wexchain.ipfs.utils.doMain
import kotlinx.android.synthetic.main.activity_loading.*
import java.math.BigInteger
import java.math.RoundingMode
import java.security.KeyStore
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RequiresApi(Build.VERSION_CODES.M)
class LoadingActivity : BaseCompatActivity() {

    private lateinit var helper: PermissionHelper
    private val WRITE_EXTERNAL_STORAGE_CODE = 102

    lateinit var keyStore: KeyStore

    lateinit var mFragmentManager: FragmentManager

    lateinit var mOrderId: String
    private var mAppToken: String? = null

    private lateinit var trustWithdrawCheckPasswdDialog: TrustWithdrawCheckPasswdDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noStatusBar()
        setContentView(R.layout.activity_loading)
        permission()
    }

    private fun permission() {
        helper = PermissionHelper(this)
        helper.requestPermissions(arrayOf(PermissionHelper.PermissionModel("存储空间", Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "我们需要您允许我们读写你的存储卡，以方便我们临时保存一些数据", WRITE_EXTERNAL_STORAGE_CODE))) {
            safeCcheck()
        }
    }

    private fun safeCcheck() {
        if (isRoot() || checkXPosed()) {
            safe_check.visibility = View.VISIBLE
//            您的手机已被root，这将导致手机运行环境不安全。2.发现危险程序xposed，请先卸载
            safe_ignore.onClick {
                SafeDialog(this)
                        .init()
//                        .setMessage(" ")
                        .setCancelClick {
                            it.dismiss()
                            finish()
                        }
                        .setConfirmClick {
                            it.dismiss()
                            safe_check.visibility = View.INVISIBLE
                            delayedStart()
                        }
            }
            safe_close.onClick {
                finish()
            }
        } else {
            delayedStart()
        }
    }


    class SafeDialog(context: Context) : Dialog(context) {

        fun setMessage(message: String): SafeDialog {
            findViewById<TextView>(R.id.safe_message).text = message
            return this
        }

        fun setCancelClick(click: (SafeDialog) -> Unit): SafeDialog {
            findViewById<TextView>(R.id.safe_cancel).onClick {
                click(this)
            }
            return this
        }

        fun setConfirmClick(click: (SafeDialog) -> Unit): SafeDialog {
            findViewById<TextView>(R.id.safe_confirm).onClick {
                click(this)
            }
            return this
        }

        fun init(): SafeDialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_check_safe, null)
            setContentView(view)
            val dialogWindow = window
            val lp = dialogWindow!!.attributes
            val d = context.resources.displayMetrics
            lp.width = (d.widthPixels * 0.8).toInt()
            lp.height = (d.heightPixels * 0.3).toInt()
            dialogWindow.attributes = lp
            window.setBackgroundDrawableResource(R.drawable.background_holding2)
            setCancelable(false)
            show()
            return this
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.onActivityResult(requestCode, resultCode, data)
    }

    private fun delayedStart() {
        if (App.get().passportRepository.passportExists) {

            val data = intent.data
            mAppToken = data?.getQueryParameter("orderId")

            if (null == mAppToken) {
                Single.timer(1500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy {
                            atLeastCreated {
                                navigateTo(HomeActivity::class.java) {
                                    putExtras(intent)
                                }
                                finish()
                            }
                        }
            } else {
                cashierContent(mAppToken!!)
            }
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.splash_logo)
            iv_logo.startAnimation(animation)
            tv_note.startAnimation(animation)
            btn_create.visibility = View.VISIBLE
            btn_import.visibility = View.VISIBLE
            btn_create.onClick {
                navigateTo(CreatePassportActivity::class.java)
            }
            btn_import.onClick {
                navigateTo(PassportImportActivity::class.java)
            }
        }
    }

    private fun cashierContent(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.cashierContent(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    mOrderId = it.order.id

                    getBalance(it.order.assetCode, mOrderId, it.order.amount, it.order.name)
                }, {
                    toast(it.message.toString())
                    Single.timer(1500, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy {
                                atLeastCreated {
                                    navigateTo(HomeActivity::class.java) {
                                        putExtras(intent)
                                    }
                                    finish()
                                }
                            }
                })
    }

    private fun getBalance(code: String, id: String, amount: String, title: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .subscribe({
                    val res = it.availableAmount.assetValue.amount.toBigDecimal().setScale(8, RoundingMode.DOWN).toPlainString()

                    val paymentCodePayDialog = PaymentCodePayDialog(this)

                    paymentCodePayDialog.setParameters(amount, title, id, "", code, res)

                    paymentCodePayDialog.setOnClickListener(object : PaymentCodePayDialog.OnClickListener {
                        override fun trustRecharge() {
                            navigateTo(TrustRechargeActivity::class.java) {
                                putExtra("code", code)
                                putExtra("url", "")
                            }
                        }

                        override fun sure() {

                            if (paymentCodePayDialog.getIsOk()) {

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
                            } else {
                                Single.timer(1500, TimeUnit.MILLISECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeBy {
                                            atLeastCreated {
                                                navigateTo(HomeActivity::class.java) {
                                                    putExtras(intent)
                                                }
                                                finish()
                                            }
                                        }
                            }
                        }

                        override fun cancel() {
                            Single.timer(1500, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeBy {
                                        atLeastCreated {
                                            navigateTo(HomeActivity::class.java) {
                                                putExtras(intent)
                                            }
                                            finish()
                                        }
                                    }
                        }
                    })
                    paymentCodePayDialog.show()


                }, {
                    toast(it.message.toString())
                    Single.timer(1500, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy {
                                atLeastCreated {
                                    navigateTo(HomeActivity::class.java) {
                                        putExtras(intent)
                                    }
                                    finish()
                                }
                            }
                })
    }


    private fun selectOption(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.selectOption(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    navigateTo(PaymentSuccessActivity::class.java) {
                        putExtra("title", it.payOptionData.order.name)
                        putExtra("id", it.payOptionData.order.id)
                        putExtra("amount", it.payOptionData.order.amount)
                        putExtra("assetCode", it.payOptionData.order.assetCode)
                    }

                }, {
                    toast(it.message.toString())

                })
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
                selectOption(mAppToken!!)
            }
        })

        fragment.setOnClickListener(object : FingerCheckDialog.OnClickListener {
            override fun cancel() {
                val getRedpacketDialog = GetRedpacketDialog(this@LoadingActivity)
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
                        selectOption(mAppToken!!)

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

}
