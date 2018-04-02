package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.support.annotation.Nullable

@Entity(tableName = CaAuthRecord.TABLE_NAME,
        primaryKeys = [CaAuthRecord.COLUMN_PASSPORT_ADDRESS, CaAuthRecord.COLUMN_REQUEST_APP_ID, CaAuthRecord.COLUMN_TIME])
data class CaAuthRecord(
        @ColumnInfo(name = COLUMN_PASSPORT_ADDRESS)
        val passportAddress: String,
        @ColumnInfo(name = COLUMN_REQUEST_APP_ID)
        val requestAppId: String,
        @ColumnInfo(name = COLUMN_REQUEST_APP_NAME)
        @Nullable
        val requestAppName: String?,
        @ColumnInfo(name = COLUMN_TIME)
        val time: Long
) {
    companion object {
        const val TABLE_NAME = "auth_records"
        const val COLUMN_PASSPORT_ADDRESS = "passport_address"
        const val COLUMN_REQUEST_APP_ID = "request_app_id"
        const val COLUMN_REQUEST_APP_NAME = "request_app_name"
        const val COLUMN_TIME = "time"
    }
}