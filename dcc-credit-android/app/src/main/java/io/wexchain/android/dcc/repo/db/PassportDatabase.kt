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

@Database(entities = [CaAuthRecord::class, AuthKeyChangeRecord::class, CurrencyMeta::class, BeneficiaryAddress::class, AddressBook::class, QueryHistory::class, TransRecord::class],
        version = PassportDatabase.VERSION_3
)
@TypeConverters(Converters::class)
abstract class PassportDatabase : RoomDatabase() {

    abstract val dao: PassportDao

    companion object {
        const val DATABASE_NAME = "passport"
        const val VERSION_1 = 1
        const val VERSION_2 = 2
        const val VERSION_3 = 3

        private val migration_1_2 = object : Migration(VERSION_1, VERSION_2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${BeneficiaryAddress.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT , PRIMARY KEY(`address`))")
            }
        }

        private val migration_2_3 = object : Migration(VERSION_2, VERSION_3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${QueryHistory.TABLE_NAME}` (`id` TEXT NOT NULL, `short_name` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `${AddressBook.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT NOT NULL,`avatar_url` TEXT, `create_time` TEXT,`update_time` TEXT, PRIMARY KEY(`address`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `${TransRecord.TABLE_NAME}` (`id` TEXT NOT NULL,`address` TEXT NOT NULL, `short_name` TEXT, `avatar_url` TEXT,`is_add` INTEGER,`create_time` TEXT,`update_time` TEXT, PRIMARY KEY(`id`))")
            }
        }

        fun createDatabase(context: Context): PassportDatabase {

            return Room.databaseBuilder(context, PassportDatabase::class.java, DATABASE_NAME)
                    .addMigrations(migration_1_2, migration_2_3)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            putPresetCurrencies(db)
                        }
                    })
                    .build()

        }

        fun putPresetCurrencies(db: SupportSQLiteDatabase) {
            val preset = AssetsRepository.preset

            val datas = ArrayList<CurrencyMeta>()

            val contentValues = ContentValues()
            preset.forEach {

                //datas.add(CurrencyMeta.from(it))


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
            /*RoomHelper.onRoomIoThread {
                dao.addOrReplaceCurrencyMeta(datas)
            }*/

        }
    }


}
