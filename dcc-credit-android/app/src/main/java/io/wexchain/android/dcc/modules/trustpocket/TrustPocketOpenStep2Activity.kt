package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.view.View
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.dcc.R

class TrustPocketOpenStep2Activity : BaseCompatActivity() {

    lateinit var pwdFirst: String

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

            }

            override fun onNull() {

            }

            override fun onFinished(pwd: String) {

                if (pwd.checkWeak()) {
                    tvTip2.text = "密码不能为连续、重复数字"
                } else {
                    pwdFirst = pwd
                    passwd1.visibility = View.VISIBLE
                    passwd2.visibility = View.VISIBLE
                }
            }
        })

        passwd2.setPwdChangeListener(object : PassWordLayout.pwdChangeListener {
            override fun onChange(pwd: String) {

            }

            override fun onNull() {

            }

            override fun onFinished(pwd: String) {
                if (pwd == pwdFirst) {
                    toast("ok")
                } else {
                    toast("请重新设置")
                    passwd1.visibility = View.VISIBLE
                    passwd2.visibility = View.GONE
                }
            }
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
