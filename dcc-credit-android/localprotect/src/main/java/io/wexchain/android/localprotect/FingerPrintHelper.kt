package io.wexchain.android.localprotect

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import io.wexchain.android.common.ANDROID_KEY_STORE
import io.wexchain.android.common.getAndroidKeyStoreLoaded
import io.wexchain.android.common.hexStrToBytes
import io.wexchain.android.common.toHex
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec

/**
 * Created by lulingzhi on 2017/12/13.
 */
object FingerPrintHelper {
    private const val KEY_NAME = "ec_local_protect_instance"

    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    fun generateProtectKeyPair(): String {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEY_STORE)
        keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_SIGN)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                        // Require the user to authenticate with a fingerprint to authorize
                        // every use of the private key
                        .setUserAuthenticationRequired(true)
                        .apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                setInvalidatedByBiometricEnrollment(true)
                            }
                        }
                        .build())
        val keyPair = keyPairGenerator.generateKeyPair()
        return keyPair.public.encoded.toHex()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    fun initProtectCryptoObject(): FingerprintManager.CryptoObject? {
        try {
            val signature = Signature.getInstance("SHA256withECDSA")
            val key = getAndroidKeyStoreLoaded().getKey(KEY_NAME, null) as PrivateKey
            signature.initSign(key)
            return FingerprintManager.CryptoObject(signature)
        } catch (e: KeyPermanentlyInvalidatedException) {
            return null
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    fun verifySuite(signature: Signature, pubKeyStr: String): Boolean {
        val bytes = ByteArray(4096)
        SecureRandom().nextBytes(bytes)
        signature.update(bytes)
        val sign = signature.sign()
        val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC)
        val pubKey = keyFactory.generatePublic(X509EncodedKeySpec(pubKeyStr.hexStrToBytes()))
        val vSignature = Signature.getInstance("SHA256withECDSA")
        vSignature.initVerify(pubKey)
        vSignature.update(bytes)
        return vSignature.verify(sign)
    }
}
