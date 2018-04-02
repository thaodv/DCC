package io.wexchain.android.dcc.repo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency

/**
 * Created by sisel on 2018/1/23.
 * known digital currencies meta data
 * erc20 tokens only now
 */
@Entity(tableName = CurrencyMeta.TABLE_NAME,
        primaryKeys = [CurrencyMeta.COLUMN_CONTRACT_ADDRESS, CurrencyMeta.COLUMN_CHAIN])
data class CurrencyMeta(
        @ColumnInfo(name = COLUMN_CHAIN)
        val chain: Chain,
        @ColumnInfo(name = COLUMN_CONTRACT_ADDRESS)
        val contractAddress: String,
        @ColumnInfo(name = COLUMN_DECIMALS)
        val decimals: Int,
        @ColumnInfo(name = COLUMN_SYMBOL)
        val symbol: String,
        @ColumnInfo(name = COLUMN_DESCRIPTION)
        val description: String,
        @ColumnInfo(name = COLUMN_ICON_URL)
        val iconUrl: String?,
        @ColumnInfo(name = COLUMN_SELECTED)
        val selected: Boolean
) {
    companion object {
        const val TABLE_NAME = "currencies"
        const val COLUMN_CHAIN = "chain"
        const val COLUMN_CONTRACT_ADDRESS = "contract_address"
        const val COLUMN_SYMBOL = "symbol"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DECIMALS = "decimals"
        const val COLUMN_ICON_URL = "icon_url"
        const val COLUMN_SELECTED = "selected"
        fun from(dc: DigitalCurrency, sel: Boolean = true): CurrencyMeta {
            return CurrencyMeta(dc.chain, dc.contractAddress!!, dc.decimals, dc.symbol, dc.description, dc.icon, sel)
        }
    }

    fun toDigitalCurrency(): DigitalCurrency {
        return DigitalCurrency(this.symbol, Chain.publicEthChain, this.decimals, this.description, this.iconUrl, this.contractAddress)
    }
}