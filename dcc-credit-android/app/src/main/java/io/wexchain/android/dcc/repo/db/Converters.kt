package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.TypeConverter
import io.wexchain.digitalwallet.Chain

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromUpdateType(updateType: AuthKeyChangeRecord.UpdateType): String = updateType.name

    @TypeConverter
    @JvmStatic
    fun toUpdateType(name:String):AuthKeyChangeRecord.UpdateType = AuthKeyChangeRecord.UpdateType.valueOf(name)

    @TypeConverter
    @JvmStatic
    fun fromChain(chain: Chain): String = chain.name

    @TypeConverter
    @JvmStatic
    fun toChain(name: String): Chain = Chain.valueOf(name)
}