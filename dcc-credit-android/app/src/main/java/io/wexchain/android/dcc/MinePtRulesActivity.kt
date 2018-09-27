package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.wexchain.android.common.kotlin.Either
import io.wexchain.android.common.kotlin.Left
import io.wexchain.android.common.kotlin.Right
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.MultiTypeListAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.view.adapter.multitype.BindingTypeViewBinder
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemEcoRewardRuleBinding
import io.wexchain.dcc.databinding.ItemEcoRewardRuleGroupBinding
import io.wexchain.dccchainservice.domain.BonusRule

class MinePtRulesActivity : BaseCompatActivity() {

    private val adapter = RulesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine_pt_rules)
        initToolbar()
        initView()
        loadRules()
    }

    private fun initView() {
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
    }

    private fun loadRules() {
        App.get().scfApi.queryMinePtRule()
                .checkonMain()
                .subscribe { rules ->
                    val grouped = rules.groupBy { it.groupCode }.entries.fold(mutableListOf<Either<String, BonusRule>>()) { acc, i ->
                        acc.add(Left(i.key))
                        acc.addAll(i.value.map { Right(it) })
                        acc
                    }
                    adapter.submitList(grouped)
                }
    }


    private class RulesAdapter : MultiTypeListAdapter<Either<String, BonusRule>>(
            defaultItemDiffCallback(),
            ::viewTypeOf,
            TYPE_GROUP to groupBinder,
            TYPE_RULE to ruleBinder
    ) {

        companion object {
            val ruleBinder = object : BindingTypeViewBinder<Either<String, BonusRule>, ItemEcoRewardRuleBinding> {
                override val layout: Int
                    get() = R.layout.item_eco_reward_rule

                override fun bindData(binding: ItemEcoRewardRuleBinding, item: Either<String, BonusRule>?) {
                    binding.rule = item?.let {
                        (it as Right<BonusRule>).value
                    }
                }
            }
            val groupBinder = object :
                    BindingTypeViewBinder<Either<String, BonusRule>, ItemEcoRewardRuleGroupBinding> {
                override val layout: Int
                    get() = R.layout.item_eco_reward_rule_group

                override fun bindData(binding: ItemEcoRewardRuleGroupBinding, item: Either<String, BonusRule>?) {
                    binding.group = item?.let {
                        (it as Left<String>).value
                    }
                }
            }

            @JvmStatic
            fun viewTypeOf(either: Either<String, BonusRule>): Int {
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
