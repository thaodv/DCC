package io.wexchain.android.dcc.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.vm.InputPhoneInfoVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.FragmentSubmitCommunicationLogBinding

class InputPhoneInfoFragment:BindFragment<FragmentSubmitCommunicationLogBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_submit_communication_log

    private var listener : Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm = getViewModel<InputPhoneInfoVm>()
        vm.clear()
        vm.submitEvent.observe(this, Observer {
            it?.let {
                listener?.onSubmitPhoneInfo(it.first,it.second)
            }
        })
        binding.vm = vm
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_phone_carrier_certification)
    }

    interface Listener{
        fun onSubmitPhoneInfo(phoneNo:String,password:String)
    }

    companion object {
        fun create(listener: Listener): InputPhoneInfoFragment {
            val fragment = InputPhoneInfoFragment()
            fragment.listener = listener
            return fragment
        }
    }
}