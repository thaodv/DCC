package io.wexchain.android.dcc.view.adapter

import android.arch.lifecycle.LifecycleOwner
import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import io.wexchain.android.common.kotlin.weak
import io.wexchain.android.dcc.view.adapter.multitype.BindingTypeViewBinder

/**
 * Created by lulingzhi on 2017/8/22.
 * for single type
 */
abstract class DataBindAdapter<B : ViewDataBinding, T>(
        @LayoutRes override val layout: Int,
        itemDiffCallback: DiffUtil.ItemCallback<T>,
        private val itemViewClickListener: ItemViewClickListener<T>? = null,
        private vararg val clickAwareViewIds: Int = intArrayOf()
) : ListAdapter<T, BindingViewHolder<B>>(itemDiffCallback),BindingTypeViewBinder<T,B> {

    var lifecycleOwner:LifecycleOwner? by weak()

    fun setList(newList: List<T>?) {
        submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> {
        return createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<B>, position: Int) {
        bindHolder(holder,getItem(position))
    }

    override fun bindingToHolder(binding: B): BindingViewHolder<B> {
        lifecycleOwner?.let {
            binding.setLifecycleOwner(it)
        }
        return if (itemViewClickListener != null) {
            ClickAwareHolder(binding, this::onPositionClick, *clickAwareViewIds)
        } else {
            BindingViewHolder(binding)
        }
    }

    private fun onPositionClick(position: Int, @IdRes viewId: Int) {
        itemViewClickListener?.onItemClick(getItem(position), position, viewId)
    }

    fun getItemOnPos(pos:Int): T {
        return getItem(pos)
    }
}
