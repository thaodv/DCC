package io.wexchain.android.dcc.domain

import android.net.Uri
import org.web3j.crypto.Credentials

data class Passport(
        val credential: Credentials,
        val authKey: AuthKey?,
        val nickname: String?,
        val avatarUri: Uri?
) {
    val address: String
        get() = credential.address
}