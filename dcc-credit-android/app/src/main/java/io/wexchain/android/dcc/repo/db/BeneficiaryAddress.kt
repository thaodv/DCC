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
@Entity(tableName = BeneficiaryAddress.TABLE_NAME)
data class BeneficiaryAddress(
        @PrimaryKey
        @ColumnInfo(name = COLUMN_ADDRESS)
        val address: String,
        @ColumnInfo(name = COLUMN_SHORT_NAME)
        val shortName: String?,
        @ColumnInfo(name = COLUM_AVATAR)
        val avatarUrl: String? = "",
        @ColumnInfo(name = COLUM_ISADD)
        val is_added: Int? = 0,
        @ColumnInfo(name = COLUM_CREATE_TIME)
        val create_time: Long? = 0
) : Abbreviated, Comparable<BeneficiaryAddress>, Serializable {

    @Ignore
    var mAbbreviation: String
    @Ignore
    var mInitial: String

    init {
        this.mAbbreviation = ContactsUtils.getAbbreviation(shortName)
        this.mInitial = mAbbreviation.substring(0, 1)
    }

    override fun getInitial(): String {
        return mInitial
    }

    override fun compareTo(b: BeneficiaryAddress): Int {
        if (mAbbreviation == b.mAbbreviation) {
            return 0
        }
        val flag: Boolean = mAbbreviation.startsWith("#")
        return if (flag xor b.mAbbreviation.startsWith("#")) {
            if (flag) 1 else -1
        } else initial.compareTo(b.initial)
    }

    companion object {
        const val TABLE_NAME = "beneficiary_address"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_SHORT_NAME = "short_name"
        const val COLUM_AVATAR = "avatar_url"
        const val COLUM_ISADD = "is_add"
        const val COLUM_CREATE_TIME = "create_time"
    }
}
