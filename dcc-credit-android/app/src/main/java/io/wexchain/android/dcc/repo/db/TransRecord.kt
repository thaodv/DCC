package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

/**
 * @author Created by Wangpeng on 2018/7/16 14:08.
 * usage:
 */
@Entity(tableName = TransRecord.TABLE_NAME)
data class TransRecord(
        @PrimaryKey
        @ColumnInfo(name = COLUNN_ID)
        val id: Long,
        @ColumnInfo(name = COLUMN_ADDRESS)
        val address: String,
        @ColumnInfo(name = COLUMN_SHORT_NAME)
        val shortName: String?,
        @ColumnInfo(name = COLUMN_AVATAR)
        val avatarUrl: String? = "",
        @ColumnInfo(name = COLUMN_ISADD)
        val is_add: Int? = 0,
        @ColumnInfo(name = COLUMN_CREATE_TIME)
        val create_time: Long? = 0,
        @ColumnInfo(name = COLUMN_UPDATE_TIME)
        val update_time: Long? = 0
) : Serializable {

    companion object {
        const val TABLE_NAME = "trans_record"
        const val COLUNN_ID = "id"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_SHORT_NAME = "short_name"
        const val COLUMN_AVATAR = "avatar_url"
        const val COLUMN_ISADD = "is_add"
        const val COLUMN_CREATE_TIME = "create_time"
        const val COLUMN_UPDATE_TIME = "update_time"
    }

}
