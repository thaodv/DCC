package io.wexchain.android.dcc.chain

import android.support.annotation.Size
import io.wexchain.android.dcc.chain.JuzixConstants.GAS_LIMIT
import io.wexchain.android.dcc.chain.JuzixConstants.GAS_PRICE
import io.wexchain.android.dcc.chain.JuzixConstants.TX_VALUE
import org.spongycastle.jcajce.provider.digest.SHA3
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Bytes32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.request.RawTransaction
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.*

object EthsFunctions {
    private val typeUInt256 = TypeReference.create(Uint256::class.java)

    /**
     * Func invocation : put key
     */
    @JvmStatic
    fun putKey(pubKey: ByteArray): Function =
        Function(
            "putKey", Arrays.asList<Type<*>>(DynamicBytes(pubKey)),
            Collections.emptyList<TypeReference<*>>()
        )

    /**
     * Func invocation : delete key
     */
    @JvmStatic
    fun deleteKey(): Function =
        Function(
            "deleteKey", Arrays.asList<Type<*>>(),
            Collections.emptyList<TypeReference<*>>()
        )

    /**
     * Func invocation : apply
     */
    @JvmStatic
    fun apply(digest1: ByteArray, digest2: ByteArray, expired: BigInteger) = Function(
        "apply",
        Arrays.asList<Type<*>>(DynamicBytes(digest1), DynamicBytes(digest2), Uint256(expired)),
        Arrays.asList<TypeReference<*>>(typeUInt256)
    )

    @JvmStatic
    fun applyLoan(
        version: BigInteger,
        idHash: ByteArray,
        applicationDigest: ByteArray,
        inputFee: BigInteger,
        receiverAddress: String
    ): Function = Function(
        "apply",
        Arrays.asList<Type<*>>(
            Uint256(version),
            Bytes32(idHash),
            DynamicBytes(applicationDigest),
            Uint256(inputFee),
            Utf8String(receiverAddress)
        ),
        Arrays.asList<TypeReference<*>>(typeUInt256)
    )

    fun cancelLoan(
        orderId:Long
    ):Function = Function(
        "cancel",
        Arrays.asList<Type<*>>(
            Uint256(BigInteger.valueOf(orderId))
        ),
        Arrays.asList<TypeReference<*>>()
    )
}

fun Function.txSigned(
    credentials: Credentials,
    address: String,
    nonce: BigInteger = privateChainNonce(credentials.address)
) =
    RawTransaction.createTransaction(
        nonce,
        GAS_PRICE,
        GAS_LIMIT,
        address,
        TX_VALUE,
        this.encoded()
    ).signedWith(credentials)

private fun Function.encoded(): String = FunctionEncoder.encode(this)
private fun RawTransaction.signedWith(credentials: Credentials) =
    Numeric.toHexString(TransactionEncoder.signMessage(this, credentials))!!

@Size(8)
private fun Long.toByteArray(): ByteArray {
    var value = this
    val result = ByteArray(8)
    for (i in 7 downTo 0) {
        result[i] = (value and 0xffL).toByte()
        value = value shr 8
    }
    return result
}

fun privateChainNonce(address: String, timestamp: Long = System.currentTimeMillis()): BigInteger {
    val byteArray = timestamp.toByteArray()
    val string = UUID.randomUUID().toString() + address
    val md = SHA3.DigestSHA3(256)
    md.update(string.toByteArray())
    val digest = md.digest()
    val nonce = ByteArray(32)
    System.arraycopy(byteArray, 0, nonce, 0, 8)
    System.arraycopy(digest, 0, nonce, 8, 24)
    return BigInteger(nonce)
}