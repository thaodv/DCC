package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

/**
 * @author Created by Wangpeng on 2018/7/10 11:06.
 * usage:
 */
@Entity(tableName = QueryHistory.TABLE_NAME)
data class QueryHistory(
        @ColumnInfo(name = COLUMN_NAME)
        var name: String,
        @PrimaryKey
        @ColumnInfo(name = COLUMN_ID)
        var id: Long

) : Serializable {

    companion object {
        const val TABLE_NAME = "query_history"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "short_name"
        const val COLUMN_TIME = "create_time"
    }
}
