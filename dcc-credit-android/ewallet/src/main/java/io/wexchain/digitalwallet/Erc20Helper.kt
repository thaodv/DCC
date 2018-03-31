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

/**
 * Created by sisel on 2018/1/22.
 */
object Erc20Helper {
    private val typeUInt256 = object : TypeReference<Uint256>() {}
    private val typeBool = object : TypeReference<Bool>() {}

    fun getBalanceCall(contractAddress: String, address: String): EthJsonTxScratch {
        return EthJsonTxScratch(to = contractAddress,
                data = FunctionEncoder.encode(Function(
                        "balanceOf",
                        listOf<Type<*>>(Address(Numeric.toBigInt(address))),
                        listOf<TypeReference<*>>(typeUInt256)
                )))
    }

    fun transferTx(to: String, value: BigInteger): String {
        return FunctionEncoder.encode(Function("transfer",
                listOf<Type<*>>(Address(Numeric.toBigInt(to)), Uint256(value)),
                listOf<TypeReference<*>>(typeBool)
        ))
    }
}