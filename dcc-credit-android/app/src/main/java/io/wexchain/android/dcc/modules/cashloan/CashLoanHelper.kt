package io.wexchain.android.dcc.modules.cashloan

import io.wexchain.dccchainservice.type.TnOrderStatus

/**
 *Created by liuyang on 2018/12/6.
 */
object CashLoanHelper {

    @JvmStatic
    fun loanRejected(status: TnOrderStatus?): Boolean {
        if (status == null) {
            return false
        }
        if (status == TnOrderStatus.REJECTED) {
            return true
        } else {
            return false
        }
    }

    @JvmStatic
    fun loanStatus(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.NONE, TnOrderStatus.DELAYED, TnOrderStatus.DELIVERIED, TnOrderStatus.CREATED -> true
            else -> false
        }
    }


}