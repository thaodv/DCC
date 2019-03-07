package io.wexchain.dcc

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.BintApi
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.InfuraApi
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.digitalwallet.api.sendRawTransaction
import io.wexchain.digitalwallet.api.transactionCount
import io.wexchain.digitalwallet.api.transactionReceipt
import io.wexchain.digitalwallet.util.gweiTowei
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.spongycastle.util.encoders.Hex
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
    lateinit var bintApi: BintApi
    @Before
    fun setup() {
        owner = Credentials.create("f233101e5e25949640694a0e640f6027688d5d4a62ef3c094495e7d689499177")
        splenderC = Credentials.create(Keys.createEcKeyPair())
        InstrumentationRegistry.getTargetContext()
        infuraApi = App.get().networking.createApi(InfuraApi::class.java, InfuraApi.getUrl)
        bintApi = App.get().networking.createApi(BintApi::class.java, BintApi.getUrl)
    }

    private val contract = "0xe8d7dcf900542bc69aeffd93d4e421a584c3c0a0"//"0xaa305e07b4e7e0b2deb892a3cbd924db1d5ba874"


    @Test
    fun testgetbiStatues() {
        val getAllowance = Erc20Helper.getBsxStatus(contract)
        val response = bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).blockingGet()
        println(Gson().toJson(response))
    }

    @Test
    fun testgetbiSaleInfo() {
        val getAllowance = Erc20Helper.getBsxSaleInfo(contract)
        val response = bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).blockingGet()
        println(Gson().toJson(response))
    }

    @Test
    fun testgetbiminAmountPerHand() {
        val getAllowance = Erc20Helper.getBsxMinAmountPerHand(contract)
        val response = bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).blockingGet()
        println(Gson().toJson(response))
    }


    @Test
    fun testgetbiInvestCeilAmount() {
        val getAllowance = Erc20Helper.getBsxInvestCeilAmount(contract)
        val response = bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).blockingGet()
        println(Gson().toJson(response))
    }


    @Test
    fun testgetbiInvestedTotalAmount() {
        val getAllowance = Erc20Helper.investedBsxTotalAmount(contract)
        val response = bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).blockingGet()
        println(Gson().toJson(response))
    }


    @Test
    fun testInvest() {
        val approve = Erc20Helper.investBsx(BigInteger.ZERO)
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
        println(response)
        //testAllowance()
    }


    @Test
    fun testAllowance() {
        val getAllowance = Erc20Helper.getAllowanceCall(contract, owner.address, splenderC.address)
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
    fun testencode() {
        var start = 127
        val bytesSizeLen = 3
        var s =
                "0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000003a37b20202270726573656e746174696f6e223a22e5b881e7949fe681af444343e7a781e993be31e69c9fe698afe99da2e59091444343e794a8e688b7e79a84e4b880e6acbee59bbae5ae9ae591a8e69c9fe38081e59bbae5ae9a444343e694b6e79b8ae79a84e695b0e5ad97e8b584e4baa7e5a29ee580bce79086e8b4a2e4baa7e59381efbc9be585b7e69c89efbc9ae69e81e4bd8ee9a38ee999a9efbc88e4b88de58c85e68bace695b0e5ad97e8b584e4baa7e69cace8baabe4bbb7e6a0bce79a84e6b6a8e8b78cefbc89efbc8ce4bf9de69cace4bf9de681afe38081e588b0e69c9fe8bf98e69cace4bb98e681afe79a84e789b9e782b9e38082e794a8e688b7e59ca8e58b9fe99b86e69c9fe58685e58fafe4bba5e8aea4e8b4ade5b881e7949fe681af444343e7a781e993be31e69c9fe79086e8b4a2e4baa7e59381efbc8ce79086e8b4a2e694b6e79b8ae4bba5444343e7a781e993bee5bda2e5bc8fe8bf94e59b9eefbc8ce5b9b3e58fb0e4b8ba444343e794a8e688b7e68f90e4be9b444343e7a781e993bee8b584e4baa7e79086e8b4a2e5928ce588b0e69c9fe887aae58aa8e8b58ee59b9ee69c8de58aa1efbc8ce4ba8ee588b0e69c9fe697a5e79a8432333a35393a3539e4b98be5898defbc8ce4bc9ae887aae58aa8e5b086e69cace98791e5928ce694b6e79b8aefbc88e694b6e79b8ae4bba5444343e7a781e993bee5bda2e5bc8fe8aea1e681afefbc89e8bf94e8bf98e588b0e794a8e688b7e79a84e992b1e58c85e59cb0e59d80e38082e69cace79086e8b4a2e4baa7e59381e794b1e59fbae4ba8e4c696e7578e78eafe5a283e690ade5bbbae79a84e9878fe58c96e4baa4e69893e7b3bbe7bb9f546f6b656e506c7573e68f90e4be9be694afe68c81efbc8ce4bd9ce4b8bae59b9ee9a688444343e794a8e688b7e79a84e7a68fe588a9efbc8ce697a0e4bbbbe4bd95e9a38ee999a9e38082e794a8e688b7e58fafe59ca8e38090e5b881e7949fe681afe68c81e4bb93e38091e4b8ade69fa5e79c8be5b881e7949fe681afe79086e8b4a2e4baa7e59381e79a84e694b6e79b8ae38082222c202022737461727454696d65223a313533343231313631392c202022656e6454696d65223a20313533343231313631392c2020226e616d65223a2022e5b881e7949fe681af444343e7a78131e69c9f222c202022706572696f64223a2032382c202022616e6e75616c52617465223a2031302c20202270726f6669744d6574686f64223a2022e588b0e69c9fe8bf98e69cace4bb98e681af227d0000000000000000000000000000000000000000000000000000000000"
        val integer = Integer.valueOf(s.substring(start, start + bytesSizeLen), 16)

        s = s.substring(start, start + integer.toInt() * 2)

        val bytes = Hex.decode(s)
        println(String(bytes))

    }

    @Test
    fun testApprove() {
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

    @Test
    fun tes() {
        val s = BigDecimal("21000000000").scaleByPowerOfTen(-9).toString()
        println(s)
    }

}
