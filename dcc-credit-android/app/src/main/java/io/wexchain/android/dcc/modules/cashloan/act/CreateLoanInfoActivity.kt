package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCreateloaninfoBinding

/**
 *Created by liuyang on 2018/12/6.
 */
class CreateLoanInfoActivity:BindActivity<ActivityCreateloaninfoBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_createloaninfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }
}