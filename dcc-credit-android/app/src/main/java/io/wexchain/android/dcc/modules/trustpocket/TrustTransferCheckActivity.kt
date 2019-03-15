package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.ipfs.utils.doMain

class TrustTransferCheckActivity : BaseCompatActivity() {

    //private val mCode get() = intent.getStringExtra("code")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_transfer_check)
        initToolbar()

        val etAddress = findViewById<EditText>(R.id.et_address)

        findViewById<Button>(R.id.bt_next).onClick {

            val address = etAddress.text.trim().toString()

            if ("" == address) {
                toast("请输入对方手机号或收币地址")
            } else {

                if (address.contains("0x")) {
                    getMemberAndMobileUserInfo(address)
                } else {
                    getMemberAndMobileUserInfo(address)
                }
            }
        }
    }

    private fun getMemberAndMobileUserInfo(text: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getMemberAndMobileUserInfo(it, text).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    navigateTo(TrustTransferActivity::class.java) {
                        putExtra("mobileUserId", it.mobileUserId)
                        putExtra("memberId", it.memberId)
                        putExtra("photoUrl", if (null == it.photoUrl) "" else it.photoUrl)
                        putExtra("nickName", it.nickName)
                        putExtra("mobile", it.mobile)
                        putExtra("address", it.address)
                        //putExtra("code", mCode)
                    }
                }, {
                    toast(it.message.toString())
                })
    }
}
