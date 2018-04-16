package io.wexchain.android.dcc.repo.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


@Dao
abstract class PassportDao {

    @Insert
    abstract fun saveAuthRecord(authRecord: CaAuthRecord): Long

    @Insert
    abstract fun saveAuthKeyChangeRecord(authKeyChangeRecord: AuthKeyChangeRecord): Long


    @Query("SELECT * FROM ${CaAuthRecord.TABLE_NAME} WHERE ${CaAuthRecord.COLUMN_PASSPORT_ADDRESS} = :address ORDER BY ${CaAuthRecord.COLUMN_TIME} DESC")
    abstract fun listAuthRecords(address: String):LiveData<List<CaAuthRecord>>

    @Query("SELECT * FROM ${AuthKeyChangeRecord.TABLE_NAME} WHERE ${AuthKeyChangeRecord.COLUMN_PASSPORT_ADDRESS} = :address ORDER BY ${AuthKeyChangeRecord.COLUMN_TIME} DESC")
    abstract fun listAuthKeyChangeRecords(address: String):LiveData<List<AuthKeyChangeRecord>>

    @Query("DELETE FROM ${CaAuthRecord.TABLE_NAME} WHERE ${CaAuthRecord.COLUMN_PASSPORT_ADDRESS} = :address")
    abstract fun deleteAuthRecordsByAddress(address: String)

    @Query("DELETE FROM ${AuthKeyChangeRecord.TABLE_NAME} WHERE ${AuthKeyChangeRecord.COLUMN_PASSPORT_ADDRESS} = :address")
    abstract fun deleteAuthKeyChangeRecordsByAddress(address: String)

    @Query("DELETE FROM ${CaAuthRecord.TABLE_NAME}")
    abstract fun deleteAuthRecords()

    @Query("DELETE FROM ${AuthKeyChangeRecord.TABLE_NAME}")
    abstract fun deleteAuthKeyChangeRecords()

    /* digital assets */

    @Query("SELECT * FROM ${CurrencyMeta.TABLE_NAME} WHERE ${CurrencyMeta.COLUMN_SELECTED} = :selected")
    abstract fun listCurrencyMeta(selected: Boolean): LiveData<List<CurrencyMeta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addSelected(currencyMeta: CurrencyMeta): Long

    @Update
    abstract fun updateCurrencyMeta(currencyMeta: CurrencyMeta): Int
}