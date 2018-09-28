package io.wexchain.android.common.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import io.reactivex.Single
import io.wexchain.android.common.R
import io.wexchain.android.common.constant.Extras2
import io.wexchain.android.common.view.FullScreenDialog

abstract class BaseCompatActivity : AppCompatActivity() {

    protected var toolbarTitle: TextView? = null
    protected var toolbar: Toolbar? = null

    protected val intendedTitle: String?
        get() = intent.getStringExtra(Extras2.EXTRA_TITLE)

    protected val currentActivity: Activity
        get() = ActivityCollector.currentActivity

    fun initToolbar(showHomeAsUp: Boolean = true): Toolbar? {
        toolbar = findViewById(R.id.toolbar)
        val tb = toolbar
        if (tb != null) {
            tb.setBackgroundColor(Color.parseColor("#FFFFFF"))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    protected fun finishAllActivity() = ActivityCollector.finishAll()


    protected fun finishActivity(vararg tClass: Class<*>) {
        ActivityCollector.finishActivitys(*tClass)
    }

    override fun onDestroy() {
        ActivityCollector.removeActivity(this)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home) {
            return handleHomePressed() || super.onOptionsItemSelected(item) || goBack()
        }
        return super.onOptionsItemSelected(item)
    }

    open fun goBack(): Boolean {
        if (!supportFragmentManager.popBackStackImmediate()) {
            goFinish()
        }
        return true
    }

    protected fun goFinish() {
        ActivityCompat.finishAfterTransition(this)
    }

    open fun handleHomePressed(): Boolean {
        return false
    }

    open fun onResourceLoaded(@IdRes id: Int) {

    }

    private var loadingDialog: FullScreenDialog? = null

    fun showLoadingDialog() {
        var d = loadingDialog
        if (d == null) {
            d = FullScreenDialog.createLoading(this)
            loadingDialog = d
        }
        d.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    fun <T> Single<T>.withLoading(): Single<T> {
        return this
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }
    }
}


