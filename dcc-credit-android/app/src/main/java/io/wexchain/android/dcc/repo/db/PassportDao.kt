package io.wexchain.android.dcc.repo.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


@Dao
abstract class PassportDao {

    @Insert
    abstract fun saveAuthRecord(authRecord: CaAuthRecord): Long

    /* digital assets */

    @Query("SELECT * FROM ${CurrencyMeta.TABLE_NAME} WHERE ${CurrencyMeta.COLUMN_SELECTED} = :selected")
    abstract fun listCurrencyMeta(selected: Boolean): LiveData<List<CurrencyMeta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addSelected(currencyMeta: CurrencyMeta): Long

    @Update
    abstract fun updateCurrencyMeta(currencyMeta: CurrencyMeta): Int
}