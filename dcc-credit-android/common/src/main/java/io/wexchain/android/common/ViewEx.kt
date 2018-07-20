package io.wexchain.android.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager


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

fun View.hideIme() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromInputMethod(this.windowToken, 0)
}

fun View.onClick(click: () -> Unit) {
    this.setOnClickListener {
        click.invoke()
    }
}