package org.web3j.crypto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Created by lulingzhi on 2017/11/13.
 */
class WalletFileDeserializer : JsonDeserializer<WalletFile> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext): WalletFile? {
        json ?: return null
        return WalletFile().apply {
            val root = json.asJsonObject
            this.address = root["address"].asString
            this.crypto = WalletFile.Crypto().apply {
                val crypto = root["crypto"].asJsonObject
                this.cipher = crypto["cipher"].asString
                this.ciphertext = crypto["ciphertext"].asString
                this.cipherparams = WalletFile.CipherParams().apply {
                    this.iv = crypto["cipherparams"].asJsonObject["iv"].asString
                }
                this.kdf = crypto["kdf"].asString
                this.kdfparams = when (kdf.trim().toLowerCase()) {
                    "scrypt" -> context.deserialize<WalletFile.ScryptKdfParams>(
                            crypto["kdfparams"],
                            WalletFile.ScryptKdfParams::class.java
                    )
                    "pbkdf2" -> context.deserialize<WalletFile.Aes128CtrKdfParams>(
                            crypto["kdfparams"],
                            WalletFile.Aes128CtrKdfParams::class.java
                    )
                    else -> throw IllegalArgumentException("kdf not supported")
                }
                this.mac = crypto["mac"].asString
            }
            this.id = root["id"].asString
            this.version = root["version"].asInt
        }
    }
}
