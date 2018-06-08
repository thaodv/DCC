package io.wexchain.android.dcc.view.adapter.multitype

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import io.wexchain.android.dcc.view.adapter.BindingViewHolder

interface BindingTypeViewBinder<T, B : ViewDataBinding> : TypeViewBinder<T, BindingViewHolder<B>> {

    @get:LayoutRes
    val layout:Int

    override fun bind(viewHolder: BindingViewHolder<B>, item: T) {
        bindData(viewHolder.binding, item)
    }

    fun bindData(binding: B, item: T?)

    override fun createViewHolder(parent: ViewGroup): BindingViewHolder<B> {
        val binding = DataBindingUtil.inflate<B>(LayoutInflater.from(parent.context), layout, parent, false)
        return bindingToHolder(binding)
    }

    fun bindingToHolder(binding: B): BindingViewHolder<B> = BindingViewHolder(binding)
}