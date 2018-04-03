package io.wexchain.android.dcc

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wexmarket.android.passport.base.BindActivity
import io.reactivex.Single
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.vm.SelectOptions
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivitySubmitBankCardBinding
import io.wexchain.dccchainservice.domain.BankCodes
import ru.solodovnikov.rx2locationmanager.LocationTime
import ru.solodovnikov.rx2locationmanager.ProviderDisabledException
import ru.solodovnikov.rx2locationmanager.RxLocationManager
import java.util.concurrent.TimeUnit

class SubmitBankCardActivity : BindActivity<ActivitySubmitBankCardBinding>() {
    override val contentLayoutId: Int = R.layout.activity_submit_bank_card

    private lateinit var rxLocation: RxLocationManager
    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        rxLocation = RxLocationManager(this)
        rxPermissions = RxPermissions(this)

        binding.inputBank!!.vm = SelectOptions().apply {
            optionTitle.set("开户行")
            options.set(BankCodes.banks)
        }
    }

    override fun onResume() {
        super.onResume()

        checkPreconditions()
    }

    @SuppressLint("MissingPermission")
    private fun checkPreconditions() {
        rxPermissions.request(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
                .firstOrError()
                .flatMap {
                    if (it) {
                        //granted
                        rxLocation.requestLocation(LocationManager.NETWORK_PROVIDER, LocationTime(10,TimeUnit.SECONDS))
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
                                is ProviderDisabledException->
                                    toast("定位服务不可用")
                                else ->
                                    toast("无法获取定位")
                            }
                            finish()
                        })
    }
}
