package io.wexchain.android.dcc.modules.trustpocket

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.checkWeak
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.common.tools.rsa.EncryptUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain
import java.math.BigInteger
import java.security.MessageDigest

class TrustPocketOpenStep2Activity : BaseCompatActivity() {

    lateinit var pwdFirst: String

    private val salt get() = intent.getStringExtra("salt")
    private val mUse get() = intent.getStringExtra("use")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_pocket_open_step2)
        initToolbar()

        if ("open" == mUse) {
            title = "开通托管钱包"
        } else {
            title = "设置新密码"
        }

        val tvTip1 = findViewById<TextView>(R.id.tv_tip1)
        val tvTip2 = findViewById<TextView>(R.id.tv_tip2)
        val passwd1 = findViewById<PassWordLayout>(R.id.password1)
        val passwd2 = findViewById<PassWordLayout>(R.id.password2)

        if (passwd2.visibility == View.VISIBLE) {
            tvTip1.text = "请再次确认支付密码"
            tvTip2.text = "请再次输入密码"
        } else {
            tvTip1.text = "请设置交易密码"
            tvTip2.text = "密码不能为连续、重复数字"
        }


        passwd1.setPwdChangeListener(object : PassWordLayout.pwdChangeListener {
            override fun onChange(pwd: String) {
                Log.e("onChange:", pwd)
            }

            override fun onNull() {

            }

            override fun onFinished(pwd: String) {
                Log.e("onFinished:", pwd)
                if (pwd.checkWeak()) {
                    tvTip2.text = "密码不能为连续、重复数字"
                } else if (pwd.length == 6) {
                    pwdFirst = pwd
                    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(passwd1.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    passwd1.visibility = View.GONE
                    passwd2.visibility = View.VISIBLE
                }
            }
        })

        passwd2.setPwdChangeListener(object : PassWordLayout.pwdChangeListener {
            override fun onChange(pwd: String) {
                Log.e("onChange:", pwd)
            }

            override fun onNull() {

            }

            override fun onFinished(pwd: String) {
                Log.e("onFinished:", pwd)
                if (pwd == pwdFirst) {

                    if ("open" == mUse) {
                        bindHostingWallet(pwd, salt)
                    } else {
                        initialPaymentPassword(pwd, salt)
                    }
                } else {
                    toast("请重新设置")
                    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(passwd2.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    passwd1.visibility = View.VISIBLE
                    passwd2.visibility = View.GONE
                    passwd1.removeAllPwd()
                    passwd2.removeAllPwd()
                }
            }
        })

    }

    private fun bindHostingWallet(pwd: String, salt: String) {

        val enpwd = EncryptUtils.getInstance().encode(BigInteger(MessageDigest.getInstance(ScfOperations.DIGEST).digest(pwd.toByteArray(Charsets.UTF_8))).toString(16) + salt, ShareUtils.getString(Extras.SP_TRUST_PUBKEY))
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.bindHostingWallet(it, enpwd, salt).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    Log.e("result:", it.mobileUserId)
                    navigateTo(TrustOpenSuccessActivity::class.java)
                    finish()
                }, {
                    toast(it.message.toString())
                })
    }

    private fun initialPaymentPassword(pwd: String, salt: String) {

        val enpwd = EncryptUtils.getInstance().encode(BigInteger(MessageDigest.getInstance(ScfOperations.DIGEST).digest(pwd.toByteArray(Charsets.UTF_8))).toString(16) + salt, ShareUtils.getString(Extras.SP_TRUST_PUBKEY))
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.initialPaymentPassword(it, enpwd, salt)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        finish()
                    } else {
                        toast(it.message.toString())
                    }

                }, {
                    toast(it.message.toString())
                })

    }
}
