package io.wexchain.android.dcc.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.warkiz.widget.IndicatorSeekBar
import io.wexchain.android.dcc.view.state.ExceedAware
import io.wexchain.dcc.R

class EISeekBar : IndicatorSeekBar, ExceedAware {
    override var isExceeded: Boolean
        set(value) {
            val changed = field != value
            field = value
            if (changed) refreshDrawableState()
        }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        @SuppressLint("CustomViewStyleable")
        isExceeded = context.obtainStyledAttributes(attrs, R.styleable.exceed_aware).run {
            val v = getBoolean(R.styleable.exceed_aware_state_exceeded, false)
            recycle()
            v
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + R.styleable.exceed_aware.size)
        if (isExceeded) {
            View.mergeDrawableStates(drawableState, R.styleable.exceed_aware)
        }
        return drawableState
    }
}