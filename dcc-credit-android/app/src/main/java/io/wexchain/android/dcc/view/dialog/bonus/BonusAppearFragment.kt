package io.wexchain.android.dcc.view.dialog.bonus

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.dcc.vm.RedeemBonusVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentBonusAppearBinding

class BonusAppearFragment: BindFragment<FragmentBonusAppearBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.fragment_bonus_appear

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = vm
    }

    private var vm: RedeemBonusVm? = null

    companion object {
        fun create(redeemBonusVm: RedeemBonusVm): BonusAppearFragment {
            return BonusAppearFragment().apply {
                this.vm = redeemBonusVm
            }
        }
    }

}
