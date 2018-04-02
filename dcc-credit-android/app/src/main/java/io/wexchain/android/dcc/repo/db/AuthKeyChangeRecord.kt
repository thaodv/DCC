package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverters

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
                UpdateType.ENABLE -> "启用统一登录"
                UpdateType.DISABLE -> "禁用统一登录"
                UpdateType.UPDATE -> "更新统一登录密钥"
            }
        }
    }
}