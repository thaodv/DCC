package worhavah.tongniucertmodule

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import worhavah.certs.tools.Base64toBitTools.stringToBitmap
import worhavah.mobilecertmodule.R
import worhavah.mobilecertmodule.databinding.FragmentTnverifyCarrierImgCodeBinding

class VerifyCarrierImgCodeFragment : BindFragment<FragmentTnverifyCarrierImgCodeBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_tnverify_carrier_img_code

    private var listener: VerifyCarrierSmsCodeFragment.Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.code = ""//clear
        // starttimer()
        binding.btnSubmitCode.setOnClickListener {
            binding.code?.let {
                listener?.onSubmitSmsCode(it, null)
            }
        }

        binding.recode.setOnClickListener {
            //  starttimer()
            //  binding.recode.setImageBitmap(stringToBitmap(listener?.reCode( )))
            onRefresh()
        }
        if (TextUtils.isEmpty(imgString)) {
            onRefresh()
        } else {
            binding.recode.setImageBitmap(stringToBitmap(imgString))
        }
    }

    fun onRefresh() {
        listener?.reCode()!!.observeOn(AndroidSchedulers.mainThread())

                .subscribe({
                    runOnMainThread {
                        binding.recode.setImageBitmap(stringToBitmap(it))
                    }
                }, {
                    toast(it.message.toString())

                })
    }

    var imgString: String = ""

    companion object {
        fun create(listener: VerifyCarrierSmsCodeFragment.Listener, imgString: String): VerifyCarrierImgCodeFragment {
            val fragment = VerifyCarrierImgCodeFragment()
            fragment.listener = listener
            fragment.imgString = imgString
            return fragment
        }
    }

}
