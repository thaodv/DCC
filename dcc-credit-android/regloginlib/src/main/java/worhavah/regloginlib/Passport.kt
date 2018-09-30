package worhavah.regloginlib

import android.net.Uri
import org.web3j.crypto.Credentials

data class Passport(
        val credential: Credentials,//
        val authKey: AuthKey?,
        var nickname: String?,
        val avatarUri: Uri?
) {
    val address: String
        get() = credential.address
}