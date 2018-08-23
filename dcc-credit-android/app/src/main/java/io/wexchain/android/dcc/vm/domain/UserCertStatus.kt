package io.wexchain.android.dcc.vm.domain

enum class UserCertStatus {
    NONE,
    INCOMPLETE,
    DONE,
    TIMEOUT,
    ;

    fun isProcessing(): Boolean {
        return this == INCOMPLETE
    }
}
