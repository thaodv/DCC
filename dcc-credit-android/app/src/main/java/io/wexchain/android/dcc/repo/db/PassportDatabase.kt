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

@Database(entities = [CaAuthRecord::class, AuthKeyChangeRecord::class, CurrencyMeta::class, BeneficiaryAddress::class, AddressBook::class, TransRecord::class],
        version = PassportDatabase.VERSION_6
)
@TypeConverters(Converters::class)
abstract class PassportDatabase : RoomDatabase() {

    abstract val dao: PassportDao

    companion object {
        const val DATABASE_NAME = "passport"
        const val VERSION_1 = 1
        const val VERSION_2 = 2
        const val VERSION_3 = 3
        const val VERSION_4 = 4
        const val VERSION_5 = 5
        const val VERSION_6 = 6

        private val migration_1_2 = object : Migration(VERSION_1, VERSION_2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${BeneficiaryAddress.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT , PRIMARY KEY(`address`))")
            }
        }

        private val migration_2_3 = object : Migration(VERSION_2, VERSION_3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${AddressBook.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT NOT NULL,`avatar_url` TEXT, `create_time` LONG,`update_time` LONG, PRIMARY KEY(`address`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `${TransRecord.TABLE_NAME}` (`id` LONG NOT NULL,`address` TEXT NOT NULL, `short_name` TEXT, `avatar_url` TEXT,`is_add` INTEGER,`create_time` LONG,`update_time` LONG, PRIMARY KEY(`id`))")
            }
        }

        private val migration_3_4 = object : Migration(VERSION_3, VERSION_4) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("DROP TABLE `${AddressBook.TABLE_NAME}`")
                database.execSQL("DROP TABLE `${TransRecord.TABLE_NAME}`")

                database.execSQL("CREATE TABLE IF NOT EXISTS `${AddressBook.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT NOT NULL,`avatar_url` TEXT, `create_time` INTEGER,`update_time` INTEGER, PRIMARY KEY(`address`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `${TransRecord.TABLE_NAME}` (`id` INTEGER NOT NULL,`address` TEXT NOT NULL, `short_name` TEXT, `avatar_url` TEXT,`is_add` INTEGER,`create_time` INTEGER,`update_time` INTEGER, PRIMARY KEY(`id`))")
            }
        }
        private val migration_4_5 = object : Migration(VERSION_4, VERSION_5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("update currencies set symbol= 'DTA' where description='DATA'")
            }
        }
        private val migration_5_6 = object : Migration(VERSION_5, VERSION_6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE currencies ADD COLUMN 'sort' INTEGER")
                database.execSQL("INSERT OR REPLACE INTO `currencies`(`chain`,`contract_address`,`decimals`,`symbol`,`description`,`icon_url`,`selected`) VALUES (Chain.publicEthChain,'0x056fd409e1d7a124bd7017459dfea2f387b6d5cd',2,'GUSD','Gemini dollar','https://open.dcc.finance/images/token_icon/Gemini_dollar@2x.png',1)")
                database.execSQL("update currencies set sort= 10000")
                database.execSQL("update currencies set sort= 5 where description='DATA'")
                database.execSQL("update currencies set sort= 10 where description='TrueUSD'")
                database.execSQL("update currencies set sort= 15 where description='Gemini dollar'")
                database.execSQL("update currencies set sort= 20 where description='BNB'")
                database.execSQL("update currencies set sort= 25 where description='HuobiToken'")
                database.execSQL("update currencies set sort= 30 where description='EOS'")
                database.execSQL("update currencies set sort= 35 where description='TRON'")
                database.execSQL("update currencies set sort= 40 where description='RUFF'")
                database.execSQL("update currencies set sort= 45 where description='AI Doctor'")
                database.execSQL("update currencies set sort= 50 where description='VenChain'")
                database.execSQL("update currencies set sort= 55 where description='OmiseGO'")
                database.execSQL("update currencies set sort= 60 where description='ZRX'")
            }
        }

        fun createDatabase(context: Context): PassportDatabase {

            return Room.databaseBuilder(context, PassportDatabase::class.java, DATABASE_NAME)
                    .addMigrations(migration_1_2, migration_2_3, migration_3_4, migration_4_5, migration_5_6)
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
                contentValues.put(CurrencyMeta.COLUMN_SORT, it.sort)
                db.insert(CurrencyMeta.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, contentValues)
            }
            /*RoomHelper.onRoomIoThread {
                dao.addOrReplaceCurrencyMeta(datas)
            }*/

        }
    }


}
