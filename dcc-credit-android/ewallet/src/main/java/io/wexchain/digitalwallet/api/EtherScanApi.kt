package io.wexchain.digitalwallet.api

import io.reactivex.Single
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.api.domain.*
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/17.
 * api proxy provided by EtherScan
 */
interface EtherScanApi {

    /* #Accounts */

    /**
     * https://api.etherscan.io/api?module=account&action=balance&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a&tag=latest&apikey=YourApiKeyToken
     */
    @GET("api?module=account&action=balance&tag=latest")
    fun getBalance(
            @Query("address") address: String
    ): Single<EtherScanResult<BigInteger>>

    /**
     * https://api.etherscan.io/api?module=account&action=balancemulti&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a,0x63a9975ba31b0b9626b34300f7f627147df1f526,0x198ef1ec325a96cc354c7266a038be8b5c558f67&tag=latest&apikey=YourApiKeyToken
     * @param addresses addresses concat with ','
     */
    @GET("api?module=account&action=balancemulti&tag=latest")
    fun getBalances(@Query("address") addresses: String): Single<EtherScanResult<List<EtherScanAccountBalance>>>


    /* #Transaction */

    /**
     * http://api.etherscan.io/api?module=account&action=txlist&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a&startblock=0&endblock=99999999&sort=asc&apikey=YourApiKeyToken
     */
    @GET("api?module=account&action=txlist")
    fun getTransactions(
            @Query("address") address: String,
            @Query("startblock") startblock: Long = 0L,
            @Query("endblock") endblock: Long = 99999999L,
            @Query("sort") sort: String = "asc"
    ): Single<EtherScanResult<List<EtherScanTransaction>>>

    /**
     * http://api.etherscan.io/api?module=account&action=txlist&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a&startblock=0&endblock=99999999&sort=asc&apikey=YourApiKeyToken
     */
    @GET("api?module=account&action=txlistinternal")
    fun getInternalTransactions(
            @Query("address") address: String,
            @Query("startblock") startblock: Long = 0L,
            @Query("endblock") endblock: Long = 99999999L,
            @Query("sort") sort: String = "asc"
    ): Single<EtherScanResult<List<EtherScanTransaction>>>

    /* #Token */

    /**
     * https://api.etherscan.io/api?module=account&action=tokenbalance&contractaddress=0x57d90b64a1a57749b0f932f1a3395792e12e7055&address=0xe04f27eb70e025b78871a2ad7eabe85e61212761&tag=latest&apikey=YourApiKeyToken
     * @param contractaddress ERC20 Token contract address
     * @param address InfuraApiToken holder address
     */
    @GET("api?module=account&action=tokenbalance&tag=latest")
    fun getTokenBalance(
            @Query("contractaddress") contractaddress: String,
            @Query("address") address: String
    ): Single<EtherScanResult<BigInteger>>

    /**
     * https://api.etherscan.io/api?module=stats&action=tokensupply&contractaddress=0x57d90b64a1a57749b0f932f1a3395792e12e7055&apikey=YourApiKeyToken
     * @param contractaddress ERC20 Token contract address
     */
    @GET("api?module=stats&action=tokensupply")
    fun getTokenTotalSupply(
            @Query("contractaddress") contractaddress: String
    ): Single<EtherScanResult<BigInteger>>

    /* proxy for geth/parity */

    /**
     * https://api.etherscan.io/api?module=proxy&action=eth_getTransactionReceipt&txhash=0x1e2910a262b1008d0616a0beb24c1a491d78771baa54a33e66065e03b1f46bc1&apikey=YourApiKeyToken
     */
    @GET("api?module=proxy&action=eth_getTransactionReceipt")
    fun getTransactionReceipt(
            @Query("txhash") txhash: String
    ): Single<EthJsonRpcResponse<EthJsonTxReceipt>>

    companion object {
        fun apiUrl(chain: Chain): String {
            return when (chain) {
                Chain.Ethereum -> "https://api.etherscan.io/"
                Chain.Rinkeby -> "https://rinkeby.etherscan.io/"
                else -> "https://${chain.name.toLowerCase()}.etherscan.io/"
            }
        }
    }
}