package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverters
import io.wexchain.android.dcc.tools.getString
import io.wexchain.dcc.R

@Entity(tableName = AuthKeyChangeRecord.TABLE_NAME,
        primaryKeys = [AuthKeyChangeRecord.COLUMN_PASSPORT_ADDRESS,AuthKeyChangeRecord.COLUMN_TIME])
data class AuthKeyChangeRecord(
        @ColumnInfo(name = COLUMN_PASSPORT_ADDRESS)
        val address:String,
        @ColumnInfo(name = COLUMN_TIME)
        val time:Long,
        @ColumnInfo(name = COLUMN_UPDATE_TYPE)
        val type:UpdateType
){

    companion object {

        const val TABLE_NAME = "auth_key_change_record"
        const val COLUMN_PASSPORT_ADDRESS = "passport_address"
        const val COLUMN_UPDATE_TYPE = "update_type"
        const val COLUMN_TIME = "time"
    }

    enum class UpdateType{
        ENABLE,DISABLE,UPDATE
        ;

        fun description():String{
            return when(this){
                UpdateType.ENABLE -> getString(R.string.enable_login)
                UpdateType.DISABLE -> getString(R.string.disable_login)
                UpdateType.UPDATE -> getString(R.string.update_private_key)
            }
        }
    }
}