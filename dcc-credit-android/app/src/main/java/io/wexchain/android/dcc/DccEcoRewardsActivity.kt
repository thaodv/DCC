package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.kotlin.Either
import io.wexchain.android.common.kotlin.Left
import io.wexchain.android.common.kotlin.Right
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.view.adapter.MultiTypeListAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.view.adapter.multitype.BindingTypeViewBinder
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDccEcoRewardsBinding
import io.wexchain.dcc.databinding.ItemEcoRewardRuleBinding
import io.wexchain.dcc.databinding.ItemEcoRewardRuleGroupBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.BusinessCodes
import io.wexchain.dccchainservice.domain.BonusRule
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Currencies
import java.math.BigDecimal
import java.math.BigInteger

class DccEcoRewardsActivity : BindActivity<ActivityDccEcoRewardsBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_dcc_eco_rewards

    private val adapter = RulesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
        initialLoad()
    }

    private fun initialLoad() {
        val scfApi = App.get().scfApi
        ScfOperations
            .withScfTokenInCurrentPassport {
                scfApi.getYesterdayEcoBonus(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({income->
                binding.incomePtStr = (income.yesterdayAmount?:BigDecimal.ZERO).toPlainString()
                binding.incomeAmountStr = "${Currencies.DCC.toDecimalAmount(income.amount?: BigInteger.ZERO).currencyToDisplayStr()}DCC"
            },{
                val e = it.cause?:it
                if (e is DccChainServiceException && e.businessCode == BusinessCodes.INVALID_STATUS){
                    binding.incomePtStr = getString(R.string.calculating)
                    binding.incomeAmountStr = getString(R.string.issuing)
                }else{
                    stackTrace(it)
                }
            })
        scfApi.queryBonusRule()
            .compose(Result.checked())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{rules->
                val grouped = rules.groupBy { it.groupCode }.entries.fold(mutableListOf<Either<String, BonusRule>>()) { acc, i->
                    acc.add(Left(i.key))
                    acc.addAll(i.value.map { Right(it) })
                    acc
                }
                binding.rules = grouped
            }
    }

    private fun initView() {
        findViewById<View>(R.id.btn_income_detail).setOnClickListener {
            navigateTo(DccEcoRewardsListActivity::class.java)
        }
        binding.rvList.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_eco_rewards, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_reward_terms -> {
                navigateTo(DccEcoRewardsTermsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class RulesAdapter:MultiTypeListAdapter<Either<String, BonusRule>>(
        defaultItemDiffCallback(),
        ::viewTypeOf,
        TYPE_GROUP to groupBinder,
        TYPE_RULE to ruleBinder
    ){

        companion object {
            val ruleBinder = object :BindingTypeViewBinder<Either<String, BonusRule>,ItemEcoRewardRuleBinding>{
                override val layout: Int
                    get() = R.layout.item_eco_reward_rule

                override fun bindData(binding: ItemEcoRewardRuleBinding, item: Either<String, BonusRule>?) {
                    binding.rule = item?.let {
                        (it as Right<BonusRule>).value
                    }
                }
            }
            val groupBinder = object :BindingTypeViewBinder<Either<String, BonusRule>,ItemEcoRewardRuleGroupBinding>{
                override val layout: Int
                    get() = R.layout.item_eco_reward_rule_group

                override fun bindData(binding: ItemEcoRewardRuleGroupBinding, item: Either<String, BonusRule>?) {
                    binding.group = item?.let {
                        (it as Left<String>).value
                    }
                }
            }

            @JvmStatic
            fun viewTypeOf(either:Either<String, BonusRule>):Int{
                return when (either) {
                    is Left -> TYPE_GROUP
                    is Right -> TYPE_RULE
                    else -> RecyclerView.INVALID_TYPE
                }
            }
            const val TYPE_RULE = 1
            const val TYPE_GROUP = 2
        }
    }
}
