package worhavah.tongniucertmodule

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import worhavah.certs.tools.Base64toBitTools.stringToBitmap
import worhavah.certs.tools.CertOperations
import worhavah.mobilecertmodule.R
import worhavah.mobilecertmodule.databinding.FragmentTnverifyCarrierImgCodeBinding
import worhavah.mobilecertmodule.databinding.FragmentTnverifyCarrierSmsCodeBinding
import java.util.concurrent.TimeUnit

class VerifyCarrierImgCodeFragment: BindFragment<FragmentTnverifyCarrierImgCodeBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_tnverify_carrier_img_code

    private var listener: VerifyCarrierSmsCodeFragment.Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.code = ""//clear
       // starttimer()
        binding.btnSubmitCode.setOnClickListener {
            binding.code?.let {
                listener?.onSubmitSmsCode(it,null)
            }
        }

        binding.recode .setOnClickListener {
          //  starttimer()
          //  binding.recode.setImageBitmap(stringToBitmap(listener?.reCode( )))
            onRefresh()
        }
        if(TextUtils.isEmpty(imgString)){
            onRefresh()
        }else{
            binding.recode.setImageBitmap(stringToBitmap(imgString))
        }
    }

    fun onRefresh(){
        listener?.reCode( )!! .observeOn(AndroidSchedulers.mainThread())

            .subscribe ({
                runOnMainThread {
                    binding.recode.setImageBitmap(stringToBitmap(it))
                }
            },{
                toast(it.message.toString())

            })
    }

    var imgString:String=""

    companion object {
        fun create(listener: VerifyCarrierSmsCodeFragment.Listener,imgString:String): VerifyCarrierImgCodeFragment {
            val fragment = VerifyCarrierImgCodeFragment()
            fragment.listener = listener
            fragment.imgString=imgString
            return fragment
        }
    }
    var mSubscription: Subscription? = null // Subscription 对象，用于取消订阅关系，防止内存泄露


}