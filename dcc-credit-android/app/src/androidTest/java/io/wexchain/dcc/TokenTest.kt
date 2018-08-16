package io.wexchain.dcc

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.InfuraApi
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.digitalwallet.api.sendRawTransaction
import io.wexchain.digitalwallet.api.transactionCount
import io.wexchain.digitalwallet.api.transactionReceipt
import io.wexchain.digitalwallet.util.gweiTowei
import io.wexchain.digitalwallet.util.weiToGwei
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.web3j.abi.FunctionEncoder
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.request.RawTransaction
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

@RunWith(AndroidJUnit4::class)
class TokenTest {

    lateinit var owner: Credentials
    lateinit var splenderC: Credentials
    lateinit var infuraApi: InfuraApi

    @Before
    fun setup() {
        owner = Credentials.create("f233101e5e25949640694a0e640f6027688d5d4a62ef3c094495e7d689499177")
        splenderC = Credentials.create(Keys.createEcKeyPair())
        InstrumentationRegistry.getTargetContext()
        infuraApi = App.get().networking.createApi(InfuraApi::class.java, InfuraApi.getUrl)
    }

    private val contract = "0xaa305e07b4e7e0b2deb892a3cbd924db1d5ba874"

    @Test
    fun testAllowance() {
        val getAllowance = Erc20Helper.getAllowanceCall(contract,owner.address,splenderC.address)
        val response = infuraApi.postCall(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ).blockingGet()
        println(Gson().toJson(response))
    }

    @Test
    fun testApprove(){
        val approve = Erc20Helper.approve(splenderC.address, BigInteger.ZERO)
        val count = infuraApi.transactionCount(owner.address).blockingGet()

        val rawTransaction = RawTransaction.createTransaction(
            Numeric.toBigInt(count),
            gweiTowei(BigDecimal("10")),
            BigInteger("100000"),
            contract,
            BigInteger.ZERO,
            FunctionEncoder.encode(approve)
        )

        val signed = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, owner))

        val txHash = infuraApi.sendRawTransaction(signed).blockingGet()
        println("sent approve tx")
        val response = infuraApi.transactionReceipt(txHash)
            .retryWhen(RetryWithDelay.createGrowth(8, 1000))
            .blockingGet()
        println("receipt got: approve tx")
        testAllowance()
    }

}