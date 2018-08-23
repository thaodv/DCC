package io.wexchain.digitalwallet

import io.wexchain.digitalwallet.api.domain.EthJsonTxScratch
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
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

    fun getAllowanceCall(contractAddress: String,owner:String,splender: String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                "allowance",
                    Arrays.asList<Type<*>>(Address(owner),Address(splender)),
                    Arrays.asList<TypeReference<*>>(typeUInt256)
                )
            )
        )
    }

    fun getStatus(contractAddress: String,owner:String,splender: String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                    "status",
                    Arrays.asList<Type<*>>( ),
                    Arrays.asList<TypeReference<*>>()
                )
            )
        )
    }

    fun getSaleInfo(contractAddress: String,owner:String,splender: String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                    "saleInfo",
                    Arrays.asList<Type<*>>( ),
                    Arrays.asList<TypeReference<*>>()
                )
            )
        )
    }

    fun getMinAmountPerHando(contractAddress: String,owner:String,splender: String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                    "minAmountPerHand",
                    Arrays.asList<Type<*>>( ),
                    Arrays.asList<TypeReference<*>>()
                )
            )
        )
    }

    fun getInvestCeilAmount(contractAddress: String,owner:String,splender: String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                    "investCeilAmount",
                    Arrays.asList<Type<*>>( ),
                    Arrays.asList<TypeReference<*>>()
                )
            )
        )
    }

    fun investedTotalAmount(contractAddress: String,owner:String,splender: String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                    "investedTotalAmount",
                    Arrays.asList<Type<*>>( ),
                    Arrays.asList<TypeReference<*>>()
                )
            )
        )
    }

    fun investedAmountMapping(contractAddress: String,userAddress:String):EthJsonTxScratch{
        return EthJsonTxScratch(
            to = contractAddress,
            data = FunctionEncoder.encode(
                Function(
                    "investedAmountMapping",
                    Arrays.asList<Type<*>>(Address(userAddress) ),
                    Arrays.asList<TypeReference<*>>()
                )
            )
        )
    }

    @JvmStatic
    fun invest(amount: BigInteger): Function = Function(
        "invest",
        Arrays.asList<Type<*>>( Uint256(amount)),
        Arrays.asList<TypeReference<*>>( )
    )

    @JvmStatic
    fun approve(splender: String, value: BigInteger): Function = Function(
        "approve",
        Arrays.asList<Type<*>>(Address(splender), Uint256(value)),
        Arrays.asList<TypeReference<*>>(typeBool)
    )

}