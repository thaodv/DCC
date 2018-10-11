package io.wexchain.digitalwallet

import cc.sisel.ewallet.BuildConfig

/**
 * Created by sisel on 2018/1/18.
 * preset currencies
 */
object Currencies {


    val Ethereum = DigitalCurrency("ETH", Chain.publicEthChain, 18, "Ethereum Foundation", "http://www.wexpass.cn/images/Contractz_icon/ethereum@2x.png", null,0)

    val FTC = DigitalCurrency("FTC", Chain.JUZIX_PRIVATE, 18, "Fitcoin", "http://www.wexpass.cn/images/Contractz_icon/Fitcoin@2x.png", BuildConfig.QX_CONTRACT_ADDRESS,0)

    val DCC = DigitalCurrency("DCC",Chain.MultiChain,18,"Distributed Credit Coin","http://open.dcc.finance/images/token_icon/Distributed_Credit_Chain@2x.png",null,0)


}
