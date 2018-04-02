package io.wexchain.android.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity


fun FragmentManager.commitTransaction(
        allowingStateLoss: Boolean = true,
        action: FragmentTransaction.() -> Unit
) {
    val transaction = this.beginTransaction()
            .apply(action)
    if (this.isStateSaved) {
        if (allowingStateLoss) {
            transaction.commitAllowingStateLoss()
        } else {
            throw IllegalStateException()
        }
    } else {
        transaction.commit()
    }
}


inline fun <reified T : ViewModel> Fragment.getViewModel(key: String? = null): T {
    val vmClass = T::class.java
    return if (key == null) {
        ViewModelProviders.of(this).get(vmClass)
    } else {
        ViewModelProviders.of(this).get(key, vmClass)
    }
}


fun Fragment.toast(text: CharSequence, context: Context? = this.context) {
    atLeastCreated {
        Pop.toast(text, context!!)
    }
}

fun Fragment.toast(@StringRes stringId: Int, context: Context? = this.context) {
    atLeastCreated {
        Pop.toast(stringId, context!!)
    }
}