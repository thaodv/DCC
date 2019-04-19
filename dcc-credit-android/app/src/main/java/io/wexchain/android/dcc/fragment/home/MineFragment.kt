package io.wexchain.android.dcc.fragment.home

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.arch.lifecycle.Observer
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.common.tools.rsa.EncryptUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.modules.mine.ModifyPassportPasswordActivity
import io.wexchain.android.dcc.modules.mine.SettingActivity
import io.wexchain.android.dcc.modules.passport.PassportExportActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketModifyPhoneActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketOpenTipActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketSettingsActivity
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.switchStatus
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.view.dialog.FingerCheckDialog
import io.wexchain.android.dcc.view.dialog.GetRedpacketDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustWithdrawCheckPasswdDialog
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.android.localprotect.FingerPrintHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentMineBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main
import java.math.BigInteger
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 *Created by liuyang on 2018/9/18.
 */
@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("SetTextI18n")
class MineFragment : BindFragment<FragmentMineBinding>() {

    private val passport by lazy {
        App.get().passportRepository
    }

    var isOpenTrustPocket: Boolean = false
    var hasBindTele: Boolean = false
    var bindStatus: Boolean = false

    override val contentLayoutId: Int get() = R.layout.fragment_mine

    lateinit var keyStore: KeyStore
    lateinit var mFragmentManager: FragmentManager

    var mOpen: Boolean = false

    private lateinit var trustWithdrawCheckPasswdDialog: TrustWithdrawCheckPasswdDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVm()
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })
    }

    override fun onResume() {
        super.onResume()
        checkBoundWechat()
        getCloudToken()
        getHostingWallet()
        getTelegramUser()

        setupClicks()
    }

    private fun checkBoundWechat() {
        binding.tvWechatStatus.text = if (GardenOperations.isBound()) getString(R.string.mine_bind_wx_text1) else getString(R.string.mine_bind_wx_text2)
        binding.tvUserWechat.onClick {
            GardenOperations.wechatLogin {
                toast(it)
            }
        }
    }

    private fun getHostingWallet() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getHostingWallet(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    // 已开户
                    if (it?.mobileUserId != null) {
                        isOpenTrustPocket = true
                        App.get().mobileUserId = it.mobileUserId
                    } else {
                        isOpenTrustPocket = false
                    }
                }, {
                    // 未开户
                    isOpenTrustPocket = false
                })
    }

    private fun getCloudToken() {
        val anim = AnimationUtils.loadAnimation(activity!!, R.anim.rotate)
        IpfsOperations.getIpfsKey()
                .checkKey()
                .io_main()
                .doOnSubscribe {
                    binding.ivCloudLoding.startAnimation(anim)
                    binding.ivCloudLoding.visibility = View.VISIBLE
                    binding.tvCloudStatus.visibility = View.INVISIBLE
                }
                .doFinally {
                    binding.ivCloudLoding.clearAnimation()
                    binding.ivCloudLoding.visibility = View.INVISIBLE
                    binding.tvCloudStatus.visibility = View.VISIBLE
                }
                .subscribeBy {
                    val ipfsKeyHash = passport.getIpfsKeyHash()
                    binding.tvCloudStatus.text = if (it.isEmpty()) getString(R.string.start_in) else getString(R.string.start_out)
                    binding.tvDataCloud.onClick {
                        if (it.isEmpty()) {
                            activity?.navigateTo(OpenCloudActivity::class.java) {
                                putExtra("activity_type", SettingActivity.NOT_OPEN_CLOUD)
                            }
                        } else {
                            if (ipfsKeyHash.isNullOrEmpty()) {
                                activity?.navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                }
                            } else {
                                if (ipfsKeyHash == it) {
                                    activity?.navigateTo(MyCloudActivity::class.java)
                                } else {
                                    passport.setIpfsKeyHash("")
                                    activity?.navigateTo(OpenCloudActivity::class.java) {
                                        putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                    }
                                }
                            }
                        }
                    }
                }
    }

    private fun setupClicks() {
        val binding = binding
        binding.tvAddressBook.onClick {
            navigateTo(AddressBookActivity::class.java)
        }
        binding.tvSetting.onClick {
            navigateTo(SettingActivity::class.java)
        }
        binding.tvPassportBackup.onClick {
            navigateTo(PassportExportActivity::class.java)
        }
        binding.tvModifyPassportPassword.onClick {
            navigateTo(ModifyPassportPasswordActivity::class.java)
        }
        binding.tvDigestPocketSetting.onClick {
            navigateTo(ModifyPassportPasswordActivity::class.java)
        }
        binding.tvTrustPocketSetting.onClick {
            if (isOpenTrustPocket) {
                navigateTo(TrustPocketSettingsActivity::class.java)
            } else {
                navigateTo(TrustPocketOpenTipActivity::class.java)
            }
        }

        binding.vSwitch.onClick {
            if (hasBindTele) {

                // 已经开启
                if (bindStatus) {
                    updateEntrustStatus(false)
                } else {
                    mOpen = !bindStatus

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
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getTelegramUser() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getTelegramUser(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.llTePay.visibility = View.VISIBLE
                    hasBindTele = true
                    bindStatus = it.enableEntrust
                    binding.tvFingerPayStatus.switchStatus = bindStatus
                    binding.tvTelName.text = "@" + if (null == it.userName) "" else it.userName

                }, {
                    binding.llTePay.visibility = View.GONE
                    hasBindTele = false
                    binding.tvFingerPayStatus.switchStatus = false
                })
    }

    private fun updateEntrustStatus(open: Boolean) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.updateEntrustStatus(it, open).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.enableEntrust) {
                        toast(getString(R.string.toast_msg6))
                    }
                    bindStatus = it.enableEntrust
                    binding.tvFingerPayStatus.switchStatus = bindStatus
                }, {
                    //binding.tvFingerPayStatus.switchStatus = false
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

    private fun supportFingerprint(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            toast(getString(R.string.toast_msg2))
            return false
        } else {
            val keyguardManager = activity!!.getSystemService(KeyguardManager::class.java)
            val fingerprintManager = activity!!.getSystemService(FingerprintManager::class.java)
            if (!fingerprintManager!!.isHardwareDetected) {
                toast(getString(R.string.toast_msg3))
                return false
            } else if (!keyguardManager!!.isKeyguardSecure) {
                toast(getString(R.string.toast_msg4))
                return false
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                toast(getString(R.string.toast_msg5))
                return false
            }
        }
        return true
    }

    private fun checkPasswd() {
        trustWithdrawCheckPasswdDialog = TrustWithdrawCheckPasswdDialog(activity!!)

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
                        updateEntrustStatus(mOpen)

                    } else if (it.result == ValidatePaymentPasswordBean.Status.REJECTED) {
                        val deleteDialog = DeleteAddressBookDialog(activity!!)
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
                        val deleteDialog = DeleteAddressBookDialog(activity!!)
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
        mFragmentManager = childFragmentManager
        fragment.show(mFragmentManager, "")

        fragment.setOnCallBack(object : FingerCheckDialog.onCallBack {
            override fun onAuthenticated() {
                updateEntrustStatus(mOpen)
            }
        })

        fragment.setOnClickListener(object : FingerCheckDialog.OnClickListener {
            override fun cancel() {
                val getRedpacketDialog = GetRedpacketDialog(activity!!)
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
