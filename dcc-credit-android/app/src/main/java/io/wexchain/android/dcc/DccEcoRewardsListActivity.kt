package io.wexchain.android.dcc

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R

class DccEcoRewardsListActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dcc_eco_rewards_list)
        initToolbar()
        initialLoad()
    }

    private fun initialLoad() {
        val scfApi = App.get().scfApi
        ScfOperations
            .withScfTokenInCurrentPassport {
                scfApi.queryEcoBonus(it, 0)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        ScfOperations
            .withScfTokenInCurrentPassport {
                scfApi.getTotalEcoBonus(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


}
