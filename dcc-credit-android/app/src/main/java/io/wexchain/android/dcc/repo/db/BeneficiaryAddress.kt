package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 收款地址簿
 */
@Entity(tableName = BeneficiaryAddress.TABLE_NAME)
data class BeneficiaryAddress(
        @PrimaryKey
        @ColumnInfo(name = COLUMN_ADDRESS)
        val address: String,
        @ColumnInfo(name = COLUMN_SHORT_NAME)
        val shortName: String?
) {

    companion object {
        const val TABLE_NAME = "beneficiary_address"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_SHORT_NAME = "short_name"
    }
}