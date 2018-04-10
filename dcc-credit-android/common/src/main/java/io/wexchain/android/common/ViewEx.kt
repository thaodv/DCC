package io.wexchain.android.common

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View


@SuppressLint("ClickableViewAccessibility")
fun <T : View> T.setInterceptScroll() {
    val l = View.OnTouchListener { v, event ->
        v ?: return@OnTouchListener false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                v.parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        v.onTouchEvent(event)
    }
    this.setOnTouchListener(l)
}