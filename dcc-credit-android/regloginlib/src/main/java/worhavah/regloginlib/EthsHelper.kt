package worhavah.regloginlib

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.WorkerThread
import io.wexchain.android.common.getAndroidKeyStoreLoaded
import worhavah.regloginlib.Net.Networkutils
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.security.auth.x500.X500Principal

object EthsHelper{
    const val ANDROID_KEY_STORE = "AndroidKeyStore"
    const val AUTH_KEY_ALGORITHM = "RSA"
    const val AUTH_SIGN_ALGORITHM = "SHA256withRSA"
    const val AUTH_KEY_SIZE = 2048
    const val ANDROID_RSA_PREFIX = "temp_rsa_"

    @WorkerThread
    fun createAndroidRSAKeyPair(keySize: Int = AUTH_KEY_SIZE,context:Context= Networkutils.context!!): Pair<KeyPair, String> {
        val keyPairGenerator = KeyPairGenerator.getInstance(AUTH_KEY_ALGORITHM, ANDROID_KEY_STORE)
        val alias = "${ANDROID_RSA_PREFIX}${System.currentTimeMillis()}"
        assert(!getAndroidKeyStoreLoaded().containsAlias(alias))
        val genSpec: AlgorithmParameterSpec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                    .setKeySize(keySize)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .build()
        } else {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance().apply {
                add(Calendar.YEAR, 100)
            }
            @Suppress("DEPRECATION")
            KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=user"))
                    .setSerialNumber(BigInteger("1337"))
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            setKeySize(keySize)
                        //else 2048 is default
                    }
                    .build()
        }
        keyPairGenerator.initialize(genSpec)
        val keyPair = keyPairGenerator.generateKeyPair()
        return keyPair to alias
    }

}
