package io.wexchain.android.dcc.tools

import java.util.regex.Pattern


private val passwordPattern = Pattern.compile("^[A-Za-z0-9`~!@#$%^&*()+=|{}':;',\"\\[\\].\\\\<>/\\-_?~]+$")// ???

fun isPasswordValid(pw: String?): Boolean {
    return pw != null && pw.length in (8..20) && passwordPattern.matcher(pw).matches()
}

fun isKeyStoreValid(ks: String?): Boolean {
    return ks != null && ks.length >= 2
}

fun isEcPrivateKeyValid(key: String?): Boolean {
    return (key != null && ((key.length == 64 && key.checkIsHex()) || (key.length == 66 && key.toLowerCase().substring(0, 2) == "0x" && key.substring(2, key.length).checkIsHex())))
}

fun String.checkIsHex(): Boolean {
    val regex = Regex("^[A-Fa-f0-9]+$")
    return this.matches(regex)
}

fun isPhoneNumValid(phoneNum:String?):Boolean{
    return phoneNum!=null && phoneNum.length == 11
}

fun isAddressShortNameValid(inputShortName: String): Boolean {
    return true
}
