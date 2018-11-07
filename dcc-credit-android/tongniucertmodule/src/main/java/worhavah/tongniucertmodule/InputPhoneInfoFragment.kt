package worhavah.tongniucertmodule

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import io.wexchain.android.common.base.BindFragment
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.digitalwallet.Currencies
import worhavah.certs.tools.CertOperations
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
        var vm = getViewModel<InputPhoneInfoVm>()
        vm.clear()

        //phone!! to pw!!
        vm.submitEvent.observe(this, Observer {
            it?.let {
                listener?.onSubmitPhoneInfo(it.first, it.second)
            }
        })
        binding.vm = vm

        binding.inputPassword.etInputText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD


        if( checkltx()){
          //  binding.inputPhone.etInputText.setText(worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.get())
            vm.phoneNo.set(worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.get())
            vm.certFee.set("前次认证流程未结束，无须重复付费")
        }else{
            Networkutils.chainGateway.getExpectedFee(ChainGateway.TN_COMMUNICATION_LOG)
                .checkonMain()
                .subscribeBy {
                    val fee = BigInteger(it)// it.toLong()
                    vm.certFee.set("${getString(R.string.cost_of_verification)}${Currencies.DCC.toDecimalAmount(fee).setScale(4, RoundingMode.DOWN).toPlainString()} DCC")
                }
        }
    }
    fun checkltx():Boolean{
        val ltx= CertOperations.certPrefs.certTNcerttxhashcode.get()
        if(!TextUtils.isEmpty(ltx)){
            if(System.currentTimeMillis()- CertOperations.certPrefs.certTNcerttxhashtime.get()<10*60*1000){
                return true
            }else{
                CertOperations.certPrefs.certTNcerttxhashcode.set("")
            }
        }
        return false
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