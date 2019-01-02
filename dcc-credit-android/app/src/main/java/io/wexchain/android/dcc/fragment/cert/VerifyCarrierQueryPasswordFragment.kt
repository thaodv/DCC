package io.wexchain.android.dcc.fragment.cert

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BindFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentVerifyCarrierQueryPasswordBinding

class VerifyCarrierQueryPasswordFragment: BindFragment<FragmentVerifyCarrierQueryPasswordBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_verify_carrier_query_password

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
