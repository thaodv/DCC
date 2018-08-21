package io.wexchain.digitalwallet

import io.wexchain.digitalwallet.api.domain.EthJsonTxScratch
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.*

/**
 * Created by sisel on 2018/1/22.
 */
object Erc20Helper {
    @JvmStatic
    private val typeUInt256 = TypeReference.create(Uint256::class.java)
    @JvmStatic
    private val typeBool = TypeReference.create(Bool::class.java)
    @JvmStatic
    private val typeBytes = TypeReference.create(Bytes32::class.java)

    fun getBalanceCall(contractAddress: String, address: String): EthJsonTxScratch {
        return EthJsonTxScratch(
                to = contractAddress,
                data = FunctionEncoder.encode(
                        Function(
                                "balanceOf",
                                listOf<Type<*>>(Address(Numeric.toBigInt(address))),
                                listOf<TypeReference<*>>(typeUInt256)
                        )
                )
        )
    }

    fun transferTx(to: String, value: BigInteger): String {
        return FunctionEncoder.encode(
                Function(
                        "transfer",
                        listOf<Type<*>>(Address(Numeric.toBigInt(to)), Uint256(value)),
                        listOf<TypeReference<*>>(typeBool)
                )
        )
    }

    fun getAllowanceCall(contractAddress: String, owner: String, splender: String): EthJsonTxScratch {
        return EthJsonTxScratch(
                to = contractAddress,
                data = FunctionEncoder.encode(
                        Function(
                                "allowance",
                                Arrays.asList<Type<*>>(Address(owner), Address(splender)),
                                Arrays.asList<TypeReference<*>>(typeUInt256)
                        )
                )
        )
    }

    fun getIpfsKey(it: String,address: String): EthJsonTxScratch {
        return EthJsonTxScratch(
                to = it,
                from = address,
                data = FunctionEncoder.encode(
                        Function("getIpfsKey",
                                Arrays.asList<Type<*>>(),
                                Arrays.asList<TypeReference<*>>(typeBytes))
                )
        )
    }

    fun putIpfsKey(sha256Key:ByteArray):Function{
        return  Function(
                "putIpfsKey",
                Arrays.asList<Type<*>>(Bytes32(sha256Key)),
                Arrays.asList<TypeReference<*>>())
    }

    fun deleteIpfsKey():Function{
        return Function(
                "deleteIpfsKey",
                Arrays.asList<Type<*>>(),
                Arrays.asList<TypeReference<*>>())
    }

    @JvmStatic
    fun approve(splender: String, value: BigInteger): Function = Function(
            "approve",
            Arrays.asList<Type<*>>(Address(splender), Uint256(value)),
            Arrays.asList<TypeReference<*>>(typeBool)
    )

}