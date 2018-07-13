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

@Database(entities = [CaAuthRecord::class, AuthKeyChangeRecord::class, CurrencyMeta::class, BeneficiaryAddress::class, QueryHistory::class],
        version = PassportDatabase.VERSION_4
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

        private val migration_1_2 = object : Migration(VERSION_1, VERSION_2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${BeneficiaryAddress.TABLE_NAME}` (`address` TEXT NOT NULL, `short_name` TEXT , PRIMARY KEY(`address`))")
            }
        }

        private val migration_2_3 = object : Migration(VERSION_2, VERSION_3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `${QueryHistory.TABLE_NAME}` (`id` INTEGER NOT NULL, `short_name` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        private val migration_3_4 = object : Migration(VERSION_3, VERSION_4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `${BeneficiaryAddress.TABLE_NAME}` ADD COLUMN '${BeneficiaryAddress.COLUM_AVATAR}' TEXT")
                database.execSQL("ALTER TABLE `${BeneficiaryAddress.TABLE_NAME}` ADD COLUMN '${BeneficiaryAddress.COLUM_ISADD}' INTEGER")
                database.execSQL("ALTER TABLE `${BeneficiaryAddress.TABLE_NAME}` ADD COLUMN '${BeneficiaryAddress.COLUM_CREATE_TIME}' LONG")
            }
        }

        fun createDatabase(context: Context): PassportDatabase {

            return Room.databaseBuilder(context, PassportDatabase::class.java, DATABASE_NAME)
                    .addMigrations(migration_1_2, migration_2_3, migration_3_4)
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
