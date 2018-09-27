package worhavah.certs

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.megvii.livenesslib.LivenessActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.base.BindFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.toast
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.idverify.domain.LivenessResult
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Currencies
import worhavah.certs.databinding.FragmentLivenessDetectionBinding
import worhavah.certs.tools.CertOperations
import worhavah.regloginlib.Net.Networkutils
import java.math.BigInteger
import java.math.RoundingMode

class LivenessDetectionFragment: BindFragment<FragmentLivenessDetectionBinding>() {

    override val contentLayoutId: Int = R.layout.fragment_liveness_detection

    private var listener:Listener? = null

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_liveness_detection)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivbg.setOnClickListener {
            val portrait = binding.portrait
            if(portrait == null){
                requestCapture()
            }else{
                listener?.onProceed(portrait)
            }
        }
        binding.btnProceed.setOnClickListener {
            val portrait = binding.portrait
            if(portrait == null){
                requestCapture()
            }else{
                listener?.onProceed(portrait)
            }
        }
        Networkutils.chainGateway.getExpectedFee(ChainGateway.BUSINESS_ID)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{it->
                    val fee = it.toLong()
                    binding.certFee = "认证费：${Currencies.DCC.toDecimalAmount(BigInteger.valueOf(fee)).setScale(4, RoundingMode.DOWN).toPlainString()} DCC"
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("reqCode = $requestCode, resCode = $resultCode")
        when (requestCode) {
            RequestCodes.CAPTURE_HEAD_PORTRAIT -> {
                val extras = data?.extras
                if (resultCode == ResultCodes.RESULT_OK && extras != null) {
                    val result =
                            Gson().fromJson(extras.getString("result"), LivenessResult::class.java)
                    val delta = extras.getString("delta")
                    @Suppress("UNCHECKED_CAST")
                    val images = extras.getSerializable("images") as? Map<String, ByteArray>
                    println("delta = $delta")
                    println("result = ${extras.getString("result")}")
                    println("images size = ${images?.size}")
                    if (result.resultcode == R.string.verify_success && images != null && images.isNotEmpty()) {
                        val imgBestBytes = images["image_best"]
//                        val imgEnvBytes = images["image_env"]
                        binding.portrait = imgBestBytes
                        if (imgBestBytes != null) {
//                            saveHeadPortraitCache(context, imgBestBytes)
                        }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun requestCapture() {
        val context = activity!!
        CertOperations.idVerifyHelper.ensureLicence()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    RxPermissions(context).request(Manifest.permission.CAMERA)
                            .subscribe {
                                if (it) {
                                    startActivityForResult(
                                            Intent(context, LivenessActivity::class.java),
                                            RequestCodes.CAPTURE_HEAD_PORTRAIT
                                    )
                                } else {
                                    toast("没有相机权限")
                                }
                            }
                }, {
                    toast("licence fail", context)
                })
    }

    interface Listener{
        fun onProceed(portrait: ByteArray)
    }

    companion object {
        fun create(listener: Listener): LivenessDetectionFragment {
            val fragment = LivenessDetectionFragment()
            fragment.listener = listener
            return fragment
        }
    }
}