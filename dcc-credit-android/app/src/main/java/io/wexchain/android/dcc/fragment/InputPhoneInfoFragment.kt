package io.wexchain.android.dcc.fragment

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.android.dcc.tools.NoDoubleClickListener
import io.wexchain.android.dcc.tools.checkonMain
import worhavah.regloginlib.tools.isPhoneNumValid
import io.wexchain.android.dcc.vm.InputPhoneInfoVm
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentSubmitCommunicationLogBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.digitalwallet.Currencies
import java.math.BigInteger

class InputPhoneInfoFragment : BindFragment<FragmentSubmitCommunicationLogBinding>() {

    override val contentLayoutId: Int = R.layout.fragment_submit_communication_log

    private var listener: Listener? = null

    lateinit var mbtSubmit: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mbtSubmit = view.findViewById(R.id.btn_submit)
        val vm = getViewModel<InputPhoneInfoVm>()
        vm.clear()

        /*vm.submitEvent.observe(this, Observer {
            it?.let {
                listener?.onSubmitPhoneInfo(it.first, it.second)
            }
        })*/

        mbtSubmit.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val phone = binding.inputPhone.text
                val pw = binding.inputPassword.text
                if (isPhoneNumValid(phone) && !pw.isNullOrBlank()) {
                    listener?.onSubmitPhoneInfo(phone!!, pw!!)
                }
            }
        })

        binding.vm = vm
        binding.inputPassword.etInputText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        App.get().chainGateway.getExpectedFee(ChainGateway.BUSINESS_ID)
                .checkonMain()
                .subscribeBy {
                    val fee = it.toLong()
                    vm.certFee.set("${getString(R.string.cost_of_verification)}${Currencies.DCC.toDecimalAmount(BigInteger.valueOf(fee)).currencyToDisplayStr()} DCC")
                }
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_phone_carrier_certification)
    }

    interface Listener {
        fun onSubmitPhoneInfo(phoneNo: String, password: String)
    }

    companion object {
        fun create(listener: Listener): InputPhoneInfoFragment {
            val fragment = InputPhoneInfoFragment()
            fragment.listener = listener
            return fragment
        }
    }
}
