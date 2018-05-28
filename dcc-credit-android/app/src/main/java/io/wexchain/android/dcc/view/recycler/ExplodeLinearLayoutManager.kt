package io.wexchain.android.dcc.view.recycler

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class ExplodeLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        @RecyclerView.Orientation orientation: Int,
        reverseLayout: Boolean
    ) : super(context, orientation, reverseLayout)

    constructor(
        context: Context, attrs: AttributeSet, defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
}