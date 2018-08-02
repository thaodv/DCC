package io.wexchain.android.dcc.view.dialog.bonus

import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.android.dcc.vm.RedeemBonusVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentBonusRedeemBinding

class BonusRedeemFragment: BindFragment<FragmentBonusRedeemBinding>(){

    override val contentLayoutId: Int
        get() = R.layout.fragment_bonus_redeem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = vm
    }

    private var vm: RedeemBonusVm? = null

    companion object {

        fun create(redeemBonusVm: RedeemBonusVm): BonusRedeemFragment {
            return BonusRedeemFragment().apply {
                this.vm = redeemBonusVm
            }
        }
    }
}