package io.wexchain.android.dcc.repo.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.wexchain.android.dcc.repo.AssetsRepository

@Database(entities = [CaAuthRecord::class, AuthKeyChangeRecord::class,CurrencyMeta::class,BeneficiaryAddress::class],
        version = PassportDatabase.VERSION_2
)
@TypeConverters(Converters::class)
abstract class PassportDatabase:RoomDatabase() {

    abstract val dao:PassportDao

    companion object {
        const val DATABASE_NAME = "passport"
        const val VERSION_1 = 1
        const val VERSION_2 = 2

        private val migration_1_2 = object :Migration(VERSION_1, VERSION_2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${BeneficiaryAddress.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT, PRIMARY KEY(`address`))")
            }
        }

        fun createDatabase(context: Context) =
                Room.databaseBuilder(context, PassportDatabase::class.java, DATABASE_NAME)
                        .addMigrations(migration_1_2)
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