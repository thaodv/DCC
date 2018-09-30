package io.wexchain.android.dcc.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.common.base.BaseCompatFragment

/**
 * Created by lulingzhi on 2017/11/24.
 */
abstract class BindFragment<T : ViewDataBinding> : BaseCompatFragment() {
    protected lateinit var binding: T

    @get:LayoutRes
    abstract val contentLayoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, contentLayoutId, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    val isBindingInitialized: Boolean
        get() {
            return this::binding.isInitialized
        }
}
