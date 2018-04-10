package io.wexchain.android.dcc.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wexmarket.android.passport.base.BindFragment
import io.reactivex.Single
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.vm.InputBankCardInfoVm
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.auth.R
import io.wexchain.auth.databinding.FragmentInputBankCardBinding
import ru.solodovnikov.rx2locationmanager.LocationTime
import ru.solodovnikov.rx2locationmanager.ProviderDisabledException
import ru.solodovnikov.rx2locationmanager.RxLocationManager
import java.util.concurrent.TimeUnit

class InputBankCardInfoFragment: BindFragment<FragmentInputBankCardBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_input_bank_card

    private lateinit var rxLocation: RxLocationManager
    private lateinit var rxPermissions: RxPermissions

    private var listener:Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = activity!!
        rxLocation = RxLocationManager(ctx)
        rxPermissions = RxPermissions(ctx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = getViewModel<InputBankCardInfoVm>().apply {
            submitEvent.observe(this@InputBankCardInfoFragment, Observer {
                it?.let { listener?.onProceed(it) }
            })
        }
        binding.vm = viewModel
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_bank_card_certification)

        checkPreconditions()
    }

    interface Listener{
        fun onProceed(bankCardInfo: BankCardInfo)

    }

    @SuppressLint("MissingPermission")
    private fun checkPreconditions() {
        rxPermissions.request(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
                .firstOrError()
                .flatMap {
                    if (it) {
                        //granted
                        rxLocation.requestLocation(LocationManager.NETWORK_PROVIDER, LocationTime(10, TimeUnit.SECONDS))
                    } else Single.error<Location>(SecurityException())
                }
                .subscribe(
                        { loc ->
                            //todo
                            println(loc)
                        },
                        {
                            //can not get location, quit
                            when(it){
                                is SecurityException ->
                                    toast("您的定位尚未授权,请到设置中允许读取位置权限")
                                is ProviderDisabledException ->
                                    toast("定位服务不可用")
                                else ->
                                    toast("无法获取定位")
                            }
//                            activity?.finish()
                        })
    }

    companion object {
        fun create(listener: Listener): InputBankCardInfoFragment {
            val fragment = InputBankCardInfoFragment()
            fragment.listener = listener
            return fragment
        }
    }
}