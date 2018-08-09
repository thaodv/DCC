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
    abstract fun listAuthRecords(address: String): LiveData<List<CaAuthRecord>>

    @Query("SELECT * FROM ${AuthKeyChangeRecord.TABLE_NAME} WHERE ${AuthKeyChangeRecord.COLUMN_PASSPORT_ADDRESS} = :address ORDER BY ${AuthKeyChangeRecord.COLUMN_TIME} DESC")
    abstract fun listAuthKeyChangeRecords(address: String): LiveData<List<AuthKeyChangeRecord>>

    @Query("DELETE FROM ${CaAuthRecord.TABLE_NAME} WHERE ${CaAuthRecord.COLUMN_PASSPORT_ADDRESS} = :address")
    abstract fun deleteAuthRecordsByAddress(address: String)

    @Query("DELETE FROM ${AuthKeyChangeRecord.TABLE_NAME} WHERE ${AuthKeyChangeRecord.COLUMN_PASSPORT_ADDRESS} = :address")
    abstract fun deleteAuthKeyChangeRecordsByAddress(address: String)

    @Query("DELETE FROM ${CaAuthRecord.TABLE_NAME}")
    abstract fun deleteAuthRecords()

    @Query("DELETE FROM ${AuthKeyChangeRecord.TABLE_NAME}")
    abstract fun deleteAuthKeyChangeRecords()

    /* digital assets */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addOrReplaceCurrencyMeta(currencyMeta: List<CurrencyMeta>)

    @Query("SELECT * FROM ${CurrencyMeta.TABLE_NAME} WHERE ${CurrencyMeta.COLUMN_SELECTED} = :selected")
    abstract fun listCurrencyMeta(selected: Boolean): LiveData<List<CurrencyMeta>>

    @Query("SELECT * FROM ${CurrencyMeta.TABLE_NAME} WHERE ${CurrencyMeta.COLUMN_SYMBOL} = :symbol")
    abstract fun getCurrencyMeta(symbol: String): CurrencyMeta

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addSelected(currencyMeta: CurrencyMeta): Long

    @Update
    abstract fun updateCurrencyMeta(currencyMeta: CurrencyMeta): Int

    @Query("SELECT * FROM ${BeneficiaryAddress.TABLE_NAME}")
    abstract fun listBeneficiaryAddresses(): LiveData<List<BeneficiaryAddress>>

    @Query("SELECT distinct ${TransRecord.COLUMN_ADDRESS},${TransRecord.COLUNN_ID},${TransRecord.COLUMN_ADDRESS},${TransRecord.COLUMN_SHORT_NAME},${TransRecord.COLUMN_AVATAR},${TransRecord.COLUMN_ISADD},${TransRecord.COLUMN_CREATE_TIME},${TransRecord.COLUMN_UPDATE_TIME} from ${TransRecord.TABLE_NAME} group by ${TransRecord.COLUMN_ADDRESS} ORDER BY ${TransRecord.COLUMN_CREATE_TIME} DESC LIMIT 10")
    abstract fun getTransRecord(): LiveData<List<TransRecord>>

    @Query("SELECT * FROM ${BeneficiaryAddress.TABLE_NAME} WHERE ${BeneficiaryAddress.COLUMN_SHORT_NAME} LIKE :name")
    abstract fun queryAddressesByShortName(name: String): LiveData<List<BeneficiaryAddress>>

    @Query("SELECT * FROM ${BeneficiaryAddress.TABLE_NAME} WHERE ${BeneficiaryAddress.COLUMN_ADDRESS} = :address")
    abstract fun getBeneficiaryAddresseByAddress(address: String): List<BeneficiaryAddress>

    /*@Query("SELECT distinct ${QueryHistory.COLUMN_NAME},${QueryHistory.COLUMN_ID} FROM ${QueryHistory.TABLE_NAME} group by ${QueryHistory.COLUMN_NAME} ORDER BY ${QueryHistory.COLUMN_ID} DESC LIMIT 6")
    abstract fun getAddressBookQueryHistory(): LiveData<List<QueryHistory>>

    @Query("DELETE FROM ${QueryHistory.TABLE_NAME}")
    abstract fun deleteAddressBookQueryHistory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addOrUpdateAddressBookQueryHistory(queryHistory: QueryHistory)*/

    @Query("DELETE FROM ${BeneficiaryAddress.TABLE_NAME}")
    abstract fun deleteBeneficiaryAddresses()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addOrUpdateBeneficiaryAddress(beneficiaryAddress: BeneficiaryAddress)

    @Query("SELECT * FROM ${TransRecord.TABLE_NAME} WHERE ${TransRecord.COLUMN_ADDRESS} = :address")
    abstract fun getTransRecordByAddress(address: String): LiveData<List<TransRecord>>

    @Update
    abstract fun updateTransRecord(record: List<TransRecord>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addTransRecord(transRecord: TransRecord): Long

    @Delete
    abstract fun removeBeneficiaryAddress(beneficiaryAddress: BeneficiaryAddress)

    @Query("SELECT * FROM ${AddressBook.TABLE_NAME}")
    abstract fun getAllAddressBook(): LiveData<List<AddressBook>>

    @Query("SELECT * FROM ${AddressBook.TABLE_NAME} WHERE ${AddressBook.COLUMN_SHORT_NAME} LIKE :name")
    abstract fun queryAddressBookByShortName(name: String): LiveData<List<AddressBook>>

    @Query("SELECT * FROM ${AddressBook.TABLE_NAME} WHERE ${AddressBook.COLUMN_ADDRESS} = :address")
    abstract fun getAddressBookByAddress(address: String): LiveData<AddressBook>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addOrUpdateAddressBook(addressBook: AddressBook)

    @Delete
    abstract fun removeAddressBook(addressBook: AddressBook)

}
