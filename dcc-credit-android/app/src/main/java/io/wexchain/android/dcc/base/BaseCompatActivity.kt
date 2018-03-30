package io.wexchain.android.dcc.base

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.auth.R

abstract class BaseCompatActivity : AppCompatActivity() {

    protected var toolbarTitle: TextView? = null
    protected var toolbar: Toolbar? = null

    protected val intendedTitle: String?
        get() = intent.getStringExtra(Extras.EXTRA_TITLE)

    fun initToolbar(showHomeAsUp: Boolean = true): Toolbar? {
        toolbar = findViewById(R.id.toolbar)
        val tb = toolbar
        if (tb != null) {
            setSupportActionBar(toolbar)
            toolbarTitle = tb.findViewById(R.id.toolbar_title)
            intendedTitle?.let { title = it }
        }
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(showHomeAsUp)
            setDisplayShowHomeEnabled(showHomeAsUp)
            if (toolbarTitle != null) {
                setDisplayShowTitleEnabled(false)
            }
        }
        return toolbar
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar = null
        toolbarTitle = null
    }

    override fun onTitleChanged(title: CharSequence?, color: Int) {
        super.onTitleChanged(title, color)
        toolbarTitle?.run {
            text = title
            if (color != Color.TRANSPARENT) setTextColor(color)
        }
    }
}