package io.wexchain.android.dcc.view.adapter

import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.view.View

class ClickAwareHolder<out B : ViewDataBinding>(binding: B,
                                                private val positionClick: (Int, Int) -> Unit,
                                                @IdRes vararg viewIds: Int = intArrayOf())
    : BindingViewHolder<B>(binding), View.OnClickListener {

    init {
        if (viewIds.isEmpty())
            binding.root.setOnClickListener(this)
        else {
            val root = binding.root
            viewIds.forEach { root.findViewById<View>(it).setOnClickListener(this@ClickAwareHolder) }
        }
    }

    override fun onClick(v: View?) {
        positionClick.invoke(adapterPosition, v?.id ?: View.NO_ID)
    }
}
