package io.wexchain.digitalwallet

import io.wexchain.digitalwallet.api.domain.EthJsonTxScratch
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.Utils
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Function
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
    private val typeBytes = TypeReference.create(DynamicBytes::class.java)
    @JvmStatic
    private val typeAddress = TypeReference.create(Address::class.java)
    @JvmStatic
    private val typeString = TypeReference.create(Utf8String::class.java)

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

    fun getIpfsKey(it: String, address: String): EthJsonTxScratch {
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

    fun getIpfsToken(it: String, address: String, address2: String): EthJsonTxScratch {
        return EthJsonTxScratch(
                to = it,
                from = address,
                data = FunctionEncoder.encode(
                        Function("getIpfsToken",
                                Arrays.asList<Type<*>>(Address(address2)),
                                Arrays.asList<TypeReference<*>>(typeAddress, typeAddress, typeUInt256, typeString, typeBytes, typeString, typeBytes, typeBytes))
                )
        )
    }

    fun dncodeResponse(): MutableList<TypeReference<Type<*>>> {
        return Utils.convert(Arrays.asList<TypeReference<*>>(typeAddress, typeAddress, typeUInt256, typeString, typeBytes, typeString, typeBytes, typeBytes))
    }

    fun putIpfsKey(sha256Key: ByteArray): Function {
        return Function(
                "putIpfsKey",
                Arrays.asList<Type<*>>(DynamicBytes(sha256Key)),
                Arrays.asList<TypeReference<*>>())
    }

    fun putIpfsToken(contractAddress: String, version: BigInteger, cipher: String, nonce: ByteArray, token: String, digest1: ByteArray, digest2: ByteArray): Function {
        return Function(
                "putIpfsToken",
                Arrays.asList<Type<*>>(Address(contractAddress), Uint256(version), Utf8String(cipher), DynamicBytes(nonce), Utf8String(token), DynamicBytes(digest1), DynamicBytes(digest2)),
                Arrays.asList<TypeReference<*>>())
    }

    fun deleteIpfsKey(): Function {
        return Function(
                "deleteIpfsKey",
                Arrays.asList<Type<*>>(),
                Arrays.asList<TypeReference<*>>())
    }

    fun deleteIpfsToken(contractAddress: String): Function {
        return Function(
                "deleteIpfsToken",
                Arrays.asList<Type<*>>(Address(contractAddress)),
                Arrays.asList<TypeReference<*>>())
    }

    @JvmStatic
    fun approve(splender: String, value: BigInteger): Function = Function(
            "approve",
            Arrays.asList<Type<*>>(Address(splender), Uint256(value)),
            Arrays.asList<TypeReference<*>>(typeBool)
    )

}