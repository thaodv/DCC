package io.wexchain.digitalwallet.api.domain.front

import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency

/**
 * Created by sisel on 2018/1/23.
 */

data class TokenMetaInfo(
        val name: String,
        val symbol: String,
        val contractAddress: String?,
        val decimals: Int,
        val iconUrl: String?
) {
    fun toDigitalCurrency(): DigitalCurrency {
        return DigitalCurrency(
                symbol = this.symbol,
                chain = Chain.publicEthChain,
                decimals = this.decimals,
                description = this.name,
                icon = this.iconUrl,
                contractAddress = this.contractAddress,
                sort = 10000
        )
    }
}
