package io.wexchain.android.dcc.modules.trans.activity

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPublic2PrivateBinding

class Public2PrivateActivity : BindActivity<ActivityPublic2PrivateBinding>() {

    override val contentLayoutId: Int = R.layout.activity_public2_private

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = getViewModel()


        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.queryExchangeCondition(it, assetCode = "DCC").observeOn(AndroidSchedulers.mainThread())

                }.subscribe({


                }, {})


    }
}
