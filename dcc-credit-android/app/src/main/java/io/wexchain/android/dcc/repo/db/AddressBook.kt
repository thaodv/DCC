package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import io.wexchain.android.dcc.view.addressbook.Abbreviated
import io.wexchain.android.dcc.view.addressbook.ContactsUtils
import java.io.Serializable

/**
 * 收款地址簿
 */
@Entity(tableName = AddressBook.TABLE_NAME)
data class AddressBook(
        @PrimaryKey
        @ColumnInfo(name = COLUMN_ADDRESS)
        val address: String,
        @ColumnInfo(name = COLUMN_SHORT_NAME)
        val shortName: String,
        @ColumnInfo(name = COLUMN_AVATAR)
        val avatarUrl: String? = "",
        @ColumnInfo(name = COLUMN_CREATE_TIME)
        val create_time: Long? = 0,
        @ColumnInfo(name = COLUMN_UPDATE_TIME)
        val update_time: Long? = 0
) : Abbreviated, Comparable<AddressBook>, Serializable {

    @Ignore
    var mAbbreviation: String = ContactsUtils.getAbbreviation(shortName)
    @Ignore
    var mInitial: String

    init {
        this.mInitial = mAbbreviation.substring(0, 1)
    }

    override fun getInitial(): String {
        return mInitial
    }

    override fun compareTo(other: AddressBook): Int {
        if (mAbbreviation == other.mAbbreviation) {
            return 0
        }
        val flag: Boolean = mAbbreviation.startsWith("#")
        return if (flag xor other.mAbbreviation.startsWith("#")) {
            if (flag) 1 else -1
        } else initial.compareTo(other.initial)
    }

    companion object {
        const val TABLE_NAME = "address_book"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_SHORT_NAME = "short_name"
        const val COLUMN_AVATAR = "avatar_url"
        const val COLUMN_CREATE_TIME = "create_time"
        const val COLUMN_UPDATE_TIME = "update_time"
    }
}
