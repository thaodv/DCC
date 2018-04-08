package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityDccExchangeBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies

class DccExchangeActivity : BindActivity<ActivityDccExchangeBinding>() {
    override val contentLayoutId: Int = R.layout.activity_dcc_exchange

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = getViewModel()
        binding.cardPublic!!.root.setOnClickListener {
            val dccPublic = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }
            navigateTo(DigitalCurrencyActivity::class.java){
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccPublic)
                putExtra(Extras.EXTRA_DC_SELECTED, true)
            }
        }
        binding.cardJuzix!!.root.setOnClickListener {
            val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
            navigateTo(DigitalCurrencyActivity::class.java){
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dccJuzix)
                putExtra(Extras.EXTRA_DC_SELECTED, true)
            }
        }
    }
}
