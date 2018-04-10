package io.wexchain.android.dcc

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wexmarket.android.barcode.decodeBitmapQr
import com.wexmarket.android.barcode.getDecodeFriendlyBitmapFromUri
import com.wexmarket.android.passport.RequestCodes
import com.wexmarket.android.passport.base.BindActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.isOnMainThread
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityQrScannerBinding
import io.wexchain.digitalwallet.util.isEthAddress
import java.util.concurrent.atomic.AtomicBoolean

class QrScannerActivity : BindActivity<ActivityQrScannerBinding>() {

    companion object {
        private const val LOG_TAG = "QrScan"
        const val EXTRA_SCAN_RESULT = "scan_result"

        const val SCAN_TYPE_UNSPECIFIED = 0x0
        const val SCAN_TYPE_AUTH_REQUEST = 0x1
        const val SCAN_TYPE_ADDRESS = 0x2

        val qrDecodeFactory = DefaultDecoderFactory(
                listOf(BarcodeFormat.QR_CODE),
                null,
                null,
                false
        )
    }

    override val contentLayoutId: Int = R.layout.activity_qr_scanner

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
                },1500L)
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
        when (scanType) {
            SCAN_TYPE_AUTH_REQUEST -> {
//                val authRequest = tryParseAuthRequest(text)
//                if (authRequest != null) {
//                    if (resultWaiting.compareAndSet(false, true)) {
//                        startActivityForResult(Intent(this, AuthenticateActivity::class.java).apply {
//                            putExtra(Extras.EXTRA_CALLBACK_URL, authRequest.callbackUrl)
//                            data = authRequest.toRequestUri()
//                        }, RequestCodes.AUTHENTICATE)
//                    }
//                } else {
//                    toast("不是有效的认证请求信息")
//                }
            }
            SCAN_TYPE_ADDRESS -> {
                if (isEthAddress(text)) {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        val striped = "0x${text.substring(text.length - 40)}"
                        this.putExtra(EXTRA_SCAN_RESULT, striped)
                    })
                    finish()
                } else {
                    toast("不是有效的以太坊钱包地址")
                }
            }
            else -> {
                setResult(Activity.RESULT_OK, Intent().apply {
                    this.putExtra(EXTRA_SCAN_RESULT, text)
                })
                finish()
            }
        }
    }

//    private fun tryParseAuthRequest(text: String): AuthRequestWithCallback? {
//        try {
//            val obj = Gson().fromJson(text, AuthRequestWithCallback::class.java)
//            return if (AuthRequestWithCallback.isValid(obj)) {
//                obj
//            } else null
//        } catch (e: Exception) {
//            return null
//        }
//    }

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
                resultWaiting.compareAndSet(true,false)
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

    override fun onDestroy() {
        super.onDestroy()
    }


}
