package com.wexmarket.android.passport.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import io.wexchain.android.dcc.base.BaseCompatActivity

/**
 * Created by lulingzhi on 2017/11/23.
 */
abstract class BindActivity<T:ViewDataBinding>: BaseCompatActivity() {

    protected lateinit var binding:T

    @get:LayoutRes
    abstract val contentLayoutId :Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,contentLayoutId)
        binding.setLifecycleOwner(this)
    }
}