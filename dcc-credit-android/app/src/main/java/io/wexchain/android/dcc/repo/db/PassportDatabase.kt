package io.wexchain.android.dcc.repo.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.wexchain.android.dcc.repo.AssetsRepository

@Database(entities = [CaAuthRecord::class, AuthKeyChangeRecord::class,CurrencyMeta::class],
        version = PassportDatabase.VERSION_1
)
@TypeConverters(Converters::class)
abstract class PassportDatabase:RoomDatabase() {

    abstract val dao:PassportDao

    companion object {
        const val DATABASE_NAME = "passport"
        const val VERSION_1 = 1

        fun createDatabase(context: Context) =
                Room.databaseBuilder(context, PassportDatabase::class.java, DATABASE_NAME)
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                putPresetCurrencies(db)
                            }
                        })
                        .build()

        private fun putPresetCurrencies(db: SupportSQLiteDatabase) {
            val preset = AssetsRepository.preset
            val contentValues = ContentValues()
            preset.forEach {
                contentValues.clear()
                contentValues.put(CurrencyMeta.COLUMN_CHAIN, it.chain.name)
                contentValues.put(CurrencyMeta.COLUMN_CONTRACT_ADDRESS, it.contractAddress)
                contentValues.put(CurrencyMeta.COLUMN_DECIMALS, it.decimals)
                contentValues.put(CurrencyMeta.COLUMN_SYMBOL, it.symbol)
                contentValues.put(CurrencyMeta.COLUMN_DESCRIPTION, it.description)
                if (it.icon == null) {
                    contentValues.putNull(CurrencyMeta.COLUMN_ICON_URL)
                } else {
                    contentValues.put(CurrencyMeta.COLUMN_ICON_URL, it.icon)
                }
                contentValues.put(CurrencyMeta.COLUMN_SELECTED, true)
                db.insert(CurrencyMeta.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, contentValues)
            }
        }
    }
}