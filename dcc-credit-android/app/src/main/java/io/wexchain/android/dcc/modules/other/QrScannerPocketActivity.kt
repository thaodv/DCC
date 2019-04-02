package io.wexchain.android.dcc.modules.other

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wexmarket.android.barcode.decodeBitmapQr
import com.wexmarket.android.barcode.getDecodeFriendlyBitmapFromUri
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.tools.rsa.EncryptUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.modules.paymentcode.PaymentSuccessActivity
import io.wexchain.android.dcc.modules.trans.activity.CreateTransactionActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketModifyPhoneActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustRechargeActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustTransferActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustWithdrawActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.paymentcode.PaymentCodePayDialog
import io.wexchain.android.dcc.view.dialog.qrcode.QScanResultIsAddressDialog
import io.wexchain.android.dcc.view.dialog.qrcode.QScanResultNotAddressDialog
import io.wexchain.android.dcc.view.dialog.trustpocket.TrustWithdrawCheckPasswdDialog
import io.wexchain.android.dcc.view.passwordview.PassWordLayout
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityQrScannerBinding
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.util.isBtcAddress
import io.wexchain.digitalwallet.util.isEthAddress
import io.wexchain.ipfs.utils.doMain
import java.math.BigInteger
import java.math.RoundingMode
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean

class QrScannerPocketActivity : BindActivity<ActivityQrScannerBinding>() {

    companion object {
        private const val LOG_TAG = "QrScan"
        const val EXTRA_SCAN_RESULT = "scan_result"

        const val SCAN_TYPE_UNSPECIFIED = 0x0
        const val SCAN_TYPE_ADDRESS = 0x1

        val qrDecodeFactory = DefaultDecoderFactory(
                listOf(BarcodeFormat.QR_CODE),
                null,
                null,
                false
        )
    }

    override val contentLayoutId: Int = R.layout.activity_qr_scanner

    lateinit var mOrderId: String

    private var mAppToken: String? = null

    private lateinit var trustWithdrawCheckPasswdDialog: TrustWithdrawCheckPasswdDialog

    private lateinit var qScanResultIsAddressDialog: QScanResultIsAddressDialog

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            val text = result?.text
            text?.let {
                assert(isOnMainThread())
                onScanResult(it)
            }
            binding.scanView.let {
                it.barcodeView.stopDecoding()
                it.postDelayed({
                    startScan()
                }, 1500L)
            }

        }

        override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
//            ld(LOG_TAG, "possible:${resultPoints?.size}")
        }
    }


    private val scanType by lazy { intent.getIntExtra(Extras.EXPECTED_SCAN_TYPE, SCAN_TYPE_UNSPECIFIED) }
    private val resultWaiting = AtomicBoolean(false)

    private fun onScanResult(text: String) {
        beepManager.playBeepSound()

        if (text.contains("0x")) {
            getMemberAndMobileUserInfo(text)
        } else {

            if (text.contains("appPayToken")) {

                mAppToken = Gson().fromJson(text, AppPayTokenBean::class.java).appPayToken
                cashierContent(mAppToken!!)

            } else {
                val qScanResultNotAddressDialog = QScanResultNotAddressDialog(this)

                qScanResultNotAddressDialog.setOnClickListener(object : QScanResultNotAddressDialog.OnClickListener {
                    override fun copy() {
                        getClipboardManager().primaryClip = ClipData.newPlainText("text", text)
                        toast(R.string.copy_succeed)
                    }
                })

                qScanResultNotAddressDialog.setParameters(text)

                qScanResultNotAddressDialog.show()
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
                })
    }

    private fun getBalance(code: String, id: String, amount: String, title: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .withLoading()
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
                                checkPasswd()
                            } else {
                            }
                        }

                        override fun cancel() {
                        }
                    })
                    paymentCodePayDialog.show()


                }, {
                    toast(it.message.toString())
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
                    val enpwd = EncryptUtils.getInstance().encode(BigInteger(MessageDigest.getInstance(ScfOperations.DIGEST).digest(pwd.toByteArray(Charsets.UTF_8))).toString(16) + it.salt, it.pubKey)

                    selectOption(mAppToken!!, enpwd, it.salt)
                }, {
                    toast(it.message.toString())
                })
    }

    private fun selectOption(id: String, encPasswd: String, salt: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.selectOption(it, id, encPasswd, salt).check()
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

    private fun getMemberAndMobileUserInfo(text: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getMemberAndMobileUserInfo(it, text).check()
                }
                .doMain()
                .withLoading()
                .subscribe({

                    qScanResultIsAddressDialog = QScanResultIsAddressDialog(this)

                    if (text == App.get().passportRepository.getCurrentPassport()!!.address) {

                    } else {
                        qScanResultIsAddressDialog.isTrust(true)
                    }

                    qScanResultIsAddressDialog.setParameter(text)

                    qScanResultIsAddressDialog.setOnClickListener(object : QScanResultIsAddressDialog.OnClickListener {
                        override fun trustTransfer() {
                            navigateTo(TrustTransferActivity::class.java) {
                                putExtra("mobileUserId", if (null == it.mobileUserId) "" else it.mobileUserId)
                                putExtra("memberId", if (null == it.memberId) "" else it.memberId)
                                putExtra("photoUrl", if (null == it.photoUrl) "" else it.photoUrl)
                                putExtra("nickName", if (null == it.nickName) "" else it.nickName)
                                putExtra("mobile", if (null == it.mobile) "" else it.mobile)
                                putExtra("address", it.address)
                                //putExtra("code", mCode)
                                finish()
                            }
                        }

                        override fun digestTransfer() {
                        }

                        override fun trustWithdraw() {
                        }
                    })
                    qScanResultIsAddressDialog.show()
                }, {
                    if (isEthAddress(text)) {
                        qScanResultIsAddressDialog = QScanResultIsAddressDialog(this)

                        qScanResultIsAddressDialog.isTrust(false)

                        qScanResultIsAddressDialog.setParameter(text)

                        qScanResultIsAddressDialog.setOnClickListener(object : QScanResultIsAddressDialog.OnClickListener {
                            override fun trustTransfer() {

                            }

                            override fun digestTransfer() {
                                navigateTo(CreateTransactionActivity::class.java) {
                                    putExtra("address", text)
                                }
                                finish()
                            }

                            override fun trustWithdraw() {
                                navigateTo(TrustWithdrawActivity::class.java) {
                                    putExtra("address", qScanResultIsAddressDialog.mTvAddress.text.toString())
                                }
                                finish()
                            }
                        })
                        qScanResultIsAddressDialog.show()
                    } else if (isBtcAddress(text)) {
                        qScanResultIsAddressDialog = QScanResultIsAddressDialog(this)

                        qScanResultIsAddressDialog.isBtc(true)

                        qScanResultIsAddressDialog.setParameter(text)

                        qScanResultIsAddressDialog.setOnClickListener(object : QScanResultIsAddressDialog.OnClickListener {
                            override fun trustTransfer() {

                            }

                            override fun digestTransfer() {

                            }

                            override fun trustWithdraw() {
                                navigateTo(TrustWithdrawActivity::class.java) {
                                    putExtra("address", qScanResultIsAddressDialog.mTvAddress.text.toString())
                                }
                                finish()
                            }
                        })
                        qScanResultIsAddressDialog.show()
                    }
                })
    }

    private lateinit var beepManager: BeepManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        setWindowExtended()
        beepManager = BeepManager(this)
        setupScan(binding.scanView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_choose_picture -> {
                requestChoosePicture()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestChoosePicture() {
        binding.scanView.barcodeView.stopDecoding()
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, RequestCodes.GET_PICTURE)
    }

    private fun setupScan(scanView: DecoratedBarcodeView) {
        scanView.barcodeView.decoderFactory = qrDecodeFactory
        startScan()
    }

    private fun startScan() {
        binding.scanView.decodeSingle(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.GET_PICTURE -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    tryReadQrCodeFromPicture(data.data)
                } else {
                    startScan()
                }
            }
            RequestCodes.AUTHENTICATE -> {
                resultWaiting.compareAndSet(true, false)
                if (resultCode == Activity.RESULT_OK) {
                    //auth request done successfully
                    finish()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun tryReadQrCodeFromPicture(data: Uri) {
        Single.just(data)
                .observeOn(Schedulers.io())
                .map {
                    val decoded = getDecodeFriendlyBitmapFromUri(data, contentResolver)
                    if (decoded.config != Bitmap.Config.ARGB_8888) {
                        decoded.copy(Bitmap.Config.ARGB_8888, false)
                    } else decoded
                }
                .observeOn(Schedulers.computation())
                .map {
                    val result = decodeBitmapQr(it)
                    result!!.text
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onScanResult(it)
                }, {
                    stackTrace(it)
                    toast("未能识别二维码")
                    startScan()
                })
    }

    override fun onResume() {
        super.onResume()

        RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe {
                    if (it) {
                        binding.scanView.resume()
                    } else {
                        toast("没有相机权限,正在退出")
                        finish()
                    }
                }
    }

    override fun onPause() {
        super.onPause()
        binding.scanView.pause()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return binding.scanView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

}
