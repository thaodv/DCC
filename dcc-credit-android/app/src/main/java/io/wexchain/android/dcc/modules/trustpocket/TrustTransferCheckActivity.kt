package io.wexchain.android.dcc.modules.trustpocket

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.constant.*
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.other.QrScannerActivity
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.ipfs.utils.doMain

class TrustTransferCheckActivity : BaseCompatActivity() {

    //private val mCode get() = intent.getStringExtra("code")

    private lateinit var mTvArea: TextView

    private lateinit var etAddress: EditText

    var mDialCode: String = "86"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_transfer_check)
        initToolbar()

        etAddress = findViewById<EditText>(R.id.et_address)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val ivScan = findViewById<ImageView>(R.id.iv_scan)

        val mBtChoose = findViewById<Button>(R.id.bt_choose)

        val mllMobile = findViewById<LinearLayout>(R.id.ll_mobile)
        val mllAddress = findViewById<LinearLayout>(R.id.ll_address)

        mTvArea = findViewById(R.id.tv_area)

        mllAddress.visibility = View.GONE
        mllMobile.visibility = View.VISIBLE

        mBtChoose.text = "通过地址转账"
        mBtChoose.onClick {
            if (mllMobile.visibility == View.VISIBLE) {
                mBtChoose.text = "通过手机号转账"
                mllAddress.visibility = View.VISIBLE
                mllMobile.visibility = View.GONE
            } else {
                mBtChoose.text = "通过地址转账"
                mllAddress.visibility = View.GONE
                mllMobile.visibility = View.VISIBLE
            }
        }

        ivScan.onClick {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
            }, RequestCodes.SCAN)
        }

        val country = resources.configuration.locale.country

        LogUtils.i("country", country)

        val mDatas: AreaCodeBean = Gson().fromJson(AreaCode.res, AreaCodeBean::class.java)
        val res = mDatas.res

        for (item in res) {
            if (item.country_code == country) {
                mDialCode = item.dial_code
            }
        }

        LogUtils.i("dialCode", mDialCode)
        mTvArea.text = "+$mDialCode"


        mTvArea.onClick {
            startActivityForResult(
                    Intent(this, SearchAreaActivity::class.java),
                    RequestCodes.CHOOSE_DIAL_CODE
            )
        }

        findViewById<Button>(R.id.bt_next).onClick {

            if (mllMobile.visibility == View.VISIBLE) {
                val address = etPhone.text.trim().toString()

                if ("" == address) {
                    toast("请输入对方手机号")
                } else {
                    getMemberAndMobileUserInfo("+$mDialCode$address")
                }
            } else {
                val address = etAddress.text.trim().toString()

                if ("" == address) {
                    toast("请输入对方钱包地址")
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
                        putExtra("mobileUserId", if (null == it.mobileUserId) "" else it.mobileUserId)
                        putExtra("memberId", if (null == it.memberId) "" else it.memberId)
                        putExtra("photoUrl", if (null == it.photoUrl) "" else it.photoUrl)
                        putExtra("nickName", if (null == it.nickName) "" else it.nickName)
                        putExtra("mobile", if (null == it.mobile) "" else it.mobile)
                        putExtra("address", it.address)
                        //putExtra("code", mCode)
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_DIAL_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mDialCode = data!!.getStringExtra("dialCode")
                    mTvArea.text = "+$mDialCode"
                }
            }
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null) {
                    etAddress.setText(address)
                    etAddress.setSelection(address.length)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
