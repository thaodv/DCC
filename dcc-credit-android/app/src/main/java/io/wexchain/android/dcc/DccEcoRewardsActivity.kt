package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDccEcoRewardsBinding
import io.wexchain.dccchainservice.domain.Result

class DccEcoRewardsActivity : BindActivity<ActivityDccEcoRewardsBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_dcc_eco_rewards

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initClicks()
        initialLoad()
    }

    private fun initialLoad() {
        val scfApi = App.get().scfApi
        ScfOperations
            .withScfTokenInCurrentPassport {
                scfApi.getYesterdayEcoBonus(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{income->
                binding.income = income
            }
        scfApi.queryBonusRule()
            .compose(Result.checked())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{rules->
                binding.rules = rules.associateBy { it.bonusCode }
            }
    }

    private fun initClicks() {
        findViewById<View>(R.id.btn_income_detail).setOnClickListener {
            navigateTo(DccEcoRewardsListActivity::class.java)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_eco_rewards, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_reward_terms -> {
                //todo replace it
                navigateTo(DccEcoRewardsTermsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
