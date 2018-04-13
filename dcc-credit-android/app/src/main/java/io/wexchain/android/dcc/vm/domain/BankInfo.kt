package io.wexchain.android.dcc.vm.domain

import android.support.annotation.DrawableRes
import io.wexchain.auth.R
import io.wexchain.dccchainservice.domain.BankCodes

data class BankInfo(
        val code: String,
        val name: String,
        @DrawableRes
        val icon: Int
) {
    companion object {
        val banks = listOf<BankInfo>(
                BankInfo(BankCodes.HXB, "中国银行",R.drawable.bank_hxb),
                BankInfo(BankCodes.ABC, "农业银行",R.drawable.bank_abc),
                BankInfo(BankCodes.CCB, "建设银行",R.drawable.bank_ccb),
                BankInfo(BankCodes.ICBC, "工商银行",R.drawable.bank_icbc),
                BankInfo(BankCodes.CMB, "招商银行",R.drawable.bank_cmb),
                BankInfo(BankCodes.CITIC, "中信银行",R.drawable.bank_citic),
                BankInfo(BankCodes.CMBC, "民生银行",R.drawable.bank_cmbc),
                BankInfo(BankCodes.CIB, "兴业银行",R.drawable.bank_cib),
                BankInfo(BankCodes.CEB, "光大银行",R.drawable.bank_ceb),
                BankInfo(BankCodes.BOS, "上海银行",R.drawable.bank_bos),
                BankInfo(BankCodes.PSBC, "邮储银行",R.drawable.bank_psbc),
                BankInfo(BankCodes.SZPAB, "平安银行",R.drawable.bank_szpab),
                BankInfo(BankCodes.SPDB, "浦发银行",R.drawable.bank_spdb),
                BankInfo(BankCodes.COMM, "交通银行",R.drawable.bank_comm),
                BankInfo(BankCodes.GDB, "广发银行",R.drawable.bank_gdb),
                BankInfo(BankCodes.HXB, "华夏银行 ", R.drawable.bank_hxb)
        )
    }
}