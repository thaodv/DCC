package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.util.Log
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.common.tools.rsa.EncryptUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.trustpocket.ValidatePaymentPasswordBean
import io.wexchain.ipfs.utils.doMain
import java.math.BigInteger
import java.security.MessageDigest

class TrustPocketModifyPwdActivity : BaseCompatActivity() {

    lateinit var mOldPwd: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_pocket_modify_pwd)
        initToolbar()

        val oldpwd = findViewById<PassWordLayout>(R.id.oldpwd)

        oldpwd.setPwdChangeListener(object : PassWordLayout.pwdChangeListener {
            override fun onChange(pwd: String) {
                Log.e("onChange:", pwd)
            }

            override fun onNull() {

            }

            override fun onFinished(pwd: String) {
                Log.e("onFinished:", pwd)

                if (pwd.length == 6) {
                    mOldPwd = pwd
                    createPayPwdSecurityContext()
                }
            }
        })
    }

    private fun createPayPwdSecurityContext() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.createPayPwdSecurityContext(it)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.systemCode == Result.SUCCESS && it.businessCode == Result.SUCCESS) {
                        prepareInputPwd()
                    } else {
                        toast(it.message.toString())
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun prepareInputPwd() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.prepareInputPwd(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    validatePaymentPassword(it.pubKey, it.salt)
                }, {
                    toast(it.message.toString())
                })
    }

    private fun validatePaymentPassword(pubKey: String, salt: String) {

        val enpwd = EncryptUtils.getInstance().encode(BigInteger(MessageDigest.getInstance(ScfOperations.DIGEST).digest(mOldPwd.toByteArray(Charsets.UTF_8))).toString(16) + salt, pubKey)
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.validatePaymentPassword(it, enpwd, salt).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.result == ValidatePaymentPasswordBean.Status.PASSED) {
                        navigateTo(TrustPocketModifyPwdRestActivity::class.java)
                        finish()
                    } else {
                        toast(it.remainValidateTimes + "")
                    }
                }, {
                    toast(it.message.toString())
                })

    }

}
