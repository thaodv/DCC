package io.wexchain.android.common

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import java.math.BigDecimal


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

fun EditText.fixPrice() {
    val view = this
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.toString()?.let {
                var res = it
                if (res.contains(".")) {
                    if (res.length - 1 - res.indexOf(".") > 2) {
                        res = res.substring(0, res.indexOf(".") + 3)
                        view.setText(res)
                        view.setSelection(res.length)
                    }
                }

                //如果.在起始位置,则起始位置自动补0
                if (res.trim().substring(0) == ".") {
                    res = "0$res";
                    view.setText(res)
                    view.setSelection(2)
                }

                //如果起始位置为0并且第二位跟的不是".",则无法后续输入
                if (res.startsWith("0") && res.trim().length > 1) {
                    if (res.substring(1, 2) != ".") {
                        view.setText(res.subSequence(0, 1))
                        view.setSelection(1)
                        return
                    }
                }
            }
        }
    })
}

fun EditText.transTips(transCount: TextView, showTips: () -> Unit, hidTips: () -> Unit) {
    val view = this
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val txt = s?.toString()
            if (txt == ".") {
                view.setText("0.")
                view.setSelection(2)
                return
            }
            if (!txt.isNullOrEmpty()) {
                val maxinp = BigDecimal(transCount.text.toString())
                val inp = BigDecimal(txt)
                val result = inp.compareTo(maxinp)
                if (result == 1) {
                    showTips.invoke()
                } else {
                    hidTips.invoke()
                }
            } else {
                hidTips.invoke()
            }
        }
    })
}