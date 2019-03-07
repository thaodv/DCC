package io.wexchain.android.dcc.modules.trustpocket

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
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
import io.wexchain.ipfs.utils.doMain
import java.security.MessageDigest

class TrustPocketOpenStep2Activity : BaseCompatActivity() {

    lateinit var pwdFirst: String

    private val salt get() = intent.getStringExtra("salt")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_pocket_open_step2)
        initToolbar()

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
                    toast("ok")


                    val enpwd = EncryptUtils.getInstance().encode(MessageDigest.getInstance(ScfOperations.DIGEST).digest(pwd.toByteArray(Charsets.UTF_8)).toString() + salt, ShareUtils.getString(Extras.SP_TRUST_PUBKEY))

                    bindHostingWallet(enpwd, salt)
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
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.bindHostingWallet(it, pwd, salt).check()
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

    fun String.checkWeak(): Boolean {
        return equalStr() || isOrderNumeric() || isOrderNumeric_()
    }

    fun String.equalStr(): Boolean {
        var flag = true
        val str = this[0]
        for (i in 0 until this.length) {
            if (str != this[i]) {
                flag = false
                break
            }
        }
        return flag
    }

    fun String.isOrderNumeric(): Boolean {
        var flag = true//如果全是连续数字返回true
        var isNumeric = true//如果全是数字返回true
        for (i in 0 until this.length) {
            if (!Character.isDigit(this[i])) {
                isNumeric = false
                break
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (i in 0 until this.length) {
                if (i > 0) {//判断如123456
                    val num = Integer.parseInt(this[i] + "")
                    val num_ = Integer.parseInt(this[i - 1] + "") + 1
                    if (num != num_) {
                        flag = false
                        break
                    }
                }
            }
        } else {
            flag = false
        }
        return flag
    }

    //不能是连续的数字--递减（如：987654、876543）连续数字返回true
    fun String.isOrderNumeric_(): Boolean {
        var flag = true//如果全是连续数字返回true
        var isNumeric = true//如果全是数字返回true
        for (i in 0 until this.length) {
            if (!Character.isDigit(this[i])) {
                isNumeric = false
                break
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (i in 0 until this.length) {
                if (i > 0) {//判断如654321
                    val num = Integer.parseInt(this[i] + "")
                    val num_ = Integer.parseInt(this[i - 1] + "") - 1
                    if (num != num_) {
                        flag = false
                        break
                    }
                }
            }
        } else {
            flag = false
        }
        return flag
    }
}
