package io.wexchain.android.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import io.wexchain.android.common.view.SimpleTextWatcher
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
    view.addTextChangedListener(object : SimpleTextWatcher() {
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
    view.addTextChangedListener(object : SimpleTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val txt = s?.toString()
            if (txt == ".") {
                view.setText("0.")
                view.setSelection(2)
                return
            }
            if (txt?.startsWith("0") == true && txt.trim().length > 1) {
                if (txt.substring(1, 2) != ".") {
                    view.setText(txt.subSequence(0, 1))
                    view.setSelection(1)
                    return
                }
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

fun onTextChanged(text: (String) -> Unit) = object : SimpleTextWatcher() {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.toString()?.let {
            text(it)
        }
    }
}

fun String.checkWeak(): Boolean {
    return equalStr() || isOrderNumeric() || isOrderNumeric_()
}

fun String.equalStr(): Boolean {
    var flag = true
    val str = this[0]
    for (i in 0 until this.length) {
        if (str != this[i]) {
            flag = false
            break
        }
    }
    return flag
}

fun String.isOrderNumeric(): Boolean {
    var flag = true//如果全是连续数字返回true
    var isNumeric = true//如果全是数字返回true
    for (i in 0 until this.length) {
        if (!Character.isDigit(this[i])) {
            isNumeric = false
            break
        }
    }
    if (isNumeric) {//如果全是数字则执行是否连续数字判断
        for (i in 0 until this.length) {
            if (i > 0) {//判断如123456
                val num = Integer.parseInt(this[i] + "")
                val num_ = Integer.parseInt(this[i - 1] + "") + 1
                if (num != num_) {
                    flag = false
                    break
                }
            }
        }
    } else {
        flag = false
    }
    return flag
}

//不能是连续的数字--递减（如：987654、876543）连续数字返回true
fun String.isOrderNumeric_(): Boolean {
    var flag = true//如果全是连续数字返回true
    var isNumeric = true//如果全是数字返回true
    for (i in 0 until this.length) {
        if (!Character.isDigit(this[i])) {
            isNumeric = false
            break
        }
    }
    if (isNumeric) {//如果全是数字则执行是否连续数字判断
        for (i in 0 until this.length) {
            if (i > 0) {//判断如654321
                val num = Integer.parseInt(this[i] + "")
                val num_ = Integer.parseInt(this[i - 1] + "") - 1
                if (num != num_) {
                    flag = false
                    break
                }
            }
        }
    } else {
        flag = false
    }
    return flag
}
