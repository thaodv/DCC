package worhavah.tongniucertmodule

import android.os.Bundle
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import worhavah.mobilecertmodule.R
import worhavah.mobilecertmodule.databinding.FragmentTnverifyCarrierQueryPasswordBinding

class VerifyCarrierQueryPasswordFragment: BindFragment<FragmentTnverifyCarrierQueryPasswordBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_tnverify_carrier_query_password

    private var listener: Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.code = ""//clear
        binding.btnSubmitCode.setOnClickListener {
            binding.code?.let {
                listener?.onSubmitQueryPwd(it)
            }
        }
    }

    interface Listener{
        fun onSubmitQueryPwd(code:String)
    }

    companion object {
        fun create(listener: Listener): VerifyCarrierQueryPasswordFragment {
            val fragment = VerifyCarrierQueryPasswordFragment()
            fragment.listener = listener
            return fragment
        }
    }
}