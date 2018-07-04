package io.wexchain.android.dcc

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMineRewardsBinding
import io.wexchain.dcc.databinding.DialogPickMineCandyBinding
import io.wexchain.dcc.databinding.ItemMineRewardBinding
import io.wexchain.dccchainservice.domain.MineCandy

class MineRewardsActivity : BindActivity<ActivityMineRewardsBinding>(), ItemViewClickListener<MineCandy> {

    override val contentLayoutId: Int
        get() = R.layout.activity_mine_rewards

    private val adapter = Adapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
        loadMineStatus()
    }

    private fun initView() {
        binding.rvList.adapter = adapter
        binding.btnAccelerateMining.setOnClickListener {
            navigateTo(MinePtRulesActivity::class.java)
        }
        binding.btnMinePtDetail.setOnClickListener {
            navigateTo(MinePtListActivity::class.java)
        }
        binding.btnMineRewardRecords.setOnClickListener {
            navigateTo(MineRewardRecordsActivity::class.java)
        }
    }

    private fun loadMineStatus() {
        //todo
        //load mine rewards list
        loadRewards()
        //load mine pts
        loadMinePts()
    }

    private fun loadMinePts() {
        ScfOperations
            .withScfTokenInCurrentPassport {
                App.get().scfApi.queryMineContributionScore(it)
            }
            .subscribe { score->
                binding.pts = score
            }
    }

    private fun loadRewards() {
        ScfOperations
            .withScfTokenInCurrentPassport(emptyList()) {
                App.get().scfApi.queryMineCandyList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            //            .onErrorReturnItem((1..16L).map {
            //                MineCandy(it,"DCC_JUZIX","WEI", BigInteger.valueOf(it),MineCandy.Status.CREATED)
            //            })
            .subscribe { list ->
                binding.rewards = list
            }
    }

    override fun onItemClick(item: MineCandy?, position: Int, viewId: Int) {
        item?.let {
            showPickCandyDialog(it)
        }
    }

    private var pickCandyDialog: PickCandyDialog? = null

    private fun showPickCandyDialog(candy: MineCandy) {
        pickCandyDialog?.dismiss()
        val dialog = PickCandyDialog.create(this, candy, View.OnClickListener { pickCandy(candy) })
        dialog.show()
        pickCandyDialog = dialog
    }

    private fun pickCandy(candy: MineCandy) {
        ScfOperations
            .withScfTokenInCurrentPassport {
                App.get().scfApi.pickMineCandy(it, candy.id)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .withLoading()
            .doFinally {
                pickCandyDialog?.dismiss()
                pickCandyDialog = null
            }
            .subscribe { _ ->
                toast("领取成功")
                loadRewards()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_eco_rewards, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_reward_terms -> {
                navigateTo(DccMineRewardsTermsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class Adapter(itemViewClickListener: ItemViewClickListener<MineCandy>) :
        DataBindAdapter<ItemMineRewardBinding, MineCandy>(
            layout = R.layout.item_mine_reward,
            itemDiffCallback = defaultItemDiffCallback(),
            itemViewClickListener = itemViewClickListener
        ) {
        override fun bindData(binding: ItemMineRewardBinding, item: MineCandy?) {
            binding.candy = item
        }

    }

    class PickCandyDialog(context: Context) : Dialog(context) {

        private lateinit var onClickListener: View.OnClickListener
        private lateinit var candy: MineCandy

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding = DataBindingUtil.inflate<DialogPickMineCandyBinding>(
                layoutInflater,
                R.layout.dialog_pick_mine_candy,
                null,
                false
            )
            binding.candy = candy
            setContentView(binding.root)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.btnConfirm.setOnClickListener(onClickListener)
        }

        companion object {
            fun create(context: Context, candy: MineCandy, onClickListener: View.OnClickListener): PickCandyDialog {
                return PickCandyDialog(context).apply {
                    this.onClickListener = onClickListener
                    this.candy = candy
                }
            }
        }
    }
}
