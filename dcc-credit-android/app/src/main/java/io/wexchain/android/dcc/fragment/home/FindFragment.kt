package io.wexchain.android.dcc.fragment.home

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BindFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentFindBinding

/**
 *Created by liuyang on 2018/9/18.
 */
class FindFragment : BindFragment<FragmentFindBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.fragment_find

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}