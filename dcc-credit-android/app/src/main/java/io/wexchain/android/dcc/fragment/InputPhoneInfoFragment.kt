package io.wexchain.android.dcc.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.InputType
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.vm.InputPhoneInfoVm
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.auth.R
import io.wexchain.auth.databinding.FragmentSubmitCommunicationLogBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Currencies
import java.math.BigInteger

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
        binding.inputPassword.etInputText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        App.get().chainGateway.getExpectedFee(ChainGateway.BUSINESS_ID)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{it->
                    val fee = it.toLong()
                    vm.certFee.set("认证费：${Currencies.DCC.toDecimalAmount(BigInteger.valueOf(fee)).currencyToDisplayStr()} DCC")
                }
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