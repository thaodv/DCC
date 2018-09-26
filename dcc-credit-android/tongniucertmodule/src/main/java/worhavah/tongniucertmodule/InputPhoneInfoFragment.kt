package worhavah.tongniucertmodule

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.InputType
import android.view.View
import com.wexmarket.android.passport.base.BindFragment
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.digitalwallet.Currencies
import worhavah.certs.vm.checkonMain
import worhavah.mobilecertmodule.R
import worhavah.mobilecertmodule.databinding.FragmentSubmitTnLogBinding
import worhavah.regloginlib.Net.Networkutils
import java.math.BigInteger
import java.math.RoundingMode

class InputPhoneInfoFragment : BindFragment<FragmentSubmitTnLogBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_submit_tn_log

    private var listener: Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vm = getViewModel<InputPhoneInfoVm>()
        vm.clear()
        //phone!! to pw!!
        vm.submitEvent.observe(this, Observer {
            it?.let {
                listener?.onSubmitPhoneInfo(it.first, it.second)
            }
        })
        binding.vm = vm
        binding.inputPassword.etInputText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        Networkutils.chainGateway.getExpectedFee(ChainGateway.TN_COMMUNICATION_LOG)
                .checkonMain()
                .subscribeBy {
                    val fee = BigInteger(it)// it.toLong()
                    vm.certFee.set("${getString(R.string.cost_of_verification)}${Currencies.DCC.toDecimalAmount(fee).setScale(4, RoundingMode.DOWN).toPlainString()} DCC")
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