package io.wexchain.android.common.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes

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