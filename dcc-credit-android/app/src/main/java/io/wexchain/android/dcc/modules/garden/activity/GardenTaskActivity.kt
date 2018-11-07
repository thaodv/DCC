package io.wexchain.android.dcc.modules.garden.activity

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGardentaskBinding

/**
 *Created by liuyang on 2018/11/2.
 */
class GardenTaskActivity : BindActivity<ActivityGardentaskBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_gardentask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
    }

    private fun initVm() {
        binding.vm = getViewModel()
    }

}