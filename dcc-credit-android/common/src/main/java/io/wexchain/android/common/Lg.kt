package io.wexchain.android.common

/**
 * simple log wrapper
 */
object Lg {
    /**
     * log enable switch
     */
    private val logPrintEnabled = BuildConfig.DEBUG

    val enabled
        get() = logPrintEnabled


    inline fun withLogEnabled(crossinline block: () -> Unit) {
        if (Lg.enabled) {
            block()
        }
    }
}

fun stackTrace(throwable: Throwable) {
    Lg.withLogEnabled {
        throwable.printStackTrace()
    }
}
